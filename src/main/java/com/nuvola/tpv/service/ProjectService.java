package com.nuvola.tpv.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import com.nuvola.tpv.model.ActivityMap;
import com.nuvola.tpv.model.DeliverableMap;
import com.nuvola.tpv.model.DepartmentRevenue;
import com.nuvola.tpv.model.MandaysService;
import com.nuvola.tpv.model.MandaysSummary;
import com.nuvola.tpv.model.MilestoneMap;
import com.nuvola.tpv.model.PersonnelMap;
import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.model.PurchaseOrder;
import com.nuvola.tpv.model.PurchaseOrderQueryStub;
import com.nuvola.tpv.model.User;
import com.nuvola.tpv.repo.MandaysServiceRepository;
import com.nuvola.tpv.repo.ProjectRepository;
import com.nuvola.tpv.repo.PurchaseOrderRepository;
import com.nuvola.tpv.repo.UserRepository;

@Component
public class ProjectService {
	private static Log LOGGER = LogFactory.getLog(ProjectService.class);
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PurchaseOrderRepository poRepository;
	 @Autowired
	 private UserService userService;
	// @Autowired
	// private PurchaseRepository purchaseRepository;
	 @Autowired
	 private MandaysServiceRepository mandaysRepository;

	public static final String INSTALLATION = "INSTALL";
	public static final String TRAINING = "TRAINING";
	public static final String MANDAYS_SERVICE = "MANDAYS";
	public static final String PURCHASE_ORDER = "BOQ";
	
	public String getLeadStage(String code) {
		String desc = "";
		switch (code) {
		case "PP":
			desc = "PROPOSAL PRESENTED";
			break;
		case "SL":
			desc = "SHORTLISTED";
			break;
		case "NOP":
			desc = "NEGOTIATION ON PROCESS";
			break;
		case "CW":
			desc = "CLOSED WIN";
			break;
		case "CL":
			desc = "CLOSED LOST";
			break;
		default:
			desc = code;
			break;
		}
		return desc;
	}

	public Project getProject(String projectId) {
		return projectRepository.findById(projectId).get();

	}

	public Collection<DepartmentRevenue> getProjectDist(String poNumber) {
		List<Project> projectList = projectRepository.findByPoNumber(poNumber);
		if (projectList == null)
			return null;

		List<DepartmentRevenue> deptRevenueList = new ArrayList<DepartmentRevenue>();
		for (Project project : projectList) {
			DepartmentRevenue revenue = new DepartmentRevenue();
			revenue.setProjectNm(project.getName());
			revenue.setDepartment(project.getDepartment());
			revenue.setService(project.getService());
			revenue.setRevenue(project.getRevenue());
			revenue.setPresalesPerson(project.getPresales1());
			revenue.setSalesPerson(project.getSales());
			revenue.setProjectCode(project.getCode());
			deptRevenueList.add(revenue);
		}
		return deptRevenueList;

	}

	public Collection<String> getAllClients() {
		List<Project> projects = projectRepository.findAllClients();
		List<PurchaseOrder> purchaseOrders = poRepository.findAllCombo();

		Set<String> clients = projects.stream().filter(n -> n.getClientName() != null).map(Project::getClientName)
				.distinct().collect(Collectors.toSet());

		Set<String> clients2 = purchaseOrders.stream().filter(n -> n.getClient() != null).map(PurchaseOrder::getClient)
				.distinct().collect(Collectors.toSet());
		clients.addAll(clients2);
		clients2.clear();
		List<String> joinClients = new ArrayList<String>();
		joinClients.addAll(clients);
		clients.clear();
		Collections.sort(joinClients);
		return joinClients;
	}

	public List<Project> getProjectsBySalesCombo(String userName, String service) {
		Optional<User> userOps = userRepository.findById(userName);
		User user = userOps.get();
		Set<String> groups = user.getGroups();
		LOGGER.debug("user groups : " + Arrays.toString(groups.toArray()));
		Set<String> users = new HashSet<String>();
		groups.forEach(n -> {
			List<User> userList = userRepository.findByGroups(n);
			users.addAll(userList.stream().map(User::getUsername).collect(Collectors.toList()));
		});
		LOGGER.debug("users : " + Arrays.toString(users.toArray()));
		List<Project> projects = CommonUtils.isEmpty(service) 
				? projectRepository.findBySalesIn(users)
				: projectRepository.findByServiceAndSalesIn(service, users);
		return projects;

	}

	public Map<String, String> getProjectsBySalesGroupCombo(String userName, String service) {
		List<Project> projects = getProjectsBySalesCombo(userName, service);
		if (projects == null || projects.isEmpty())
			return null;
		Map<String, String> projectMap = new TreeMap<String, String>();
		projects.forEach((k) -> projectMap.put(k.getId(), k.getName()));
		return projectMap;
	}
	
	public ByteArrayResource downloadProject(String projectId) throws Exception {
		LOGGER.debug("Invoking downloadProjectFull..");
		return buildExcelDocument(projectId);
	}

	protected ByteArrayResource buildExcelDocument(String projectId) throws Exception {
		
		ByteArrayResource resource = null;
		Optional<Project> opt = projectRepository.findById(projectId);
		Project project = opt.get();
		
		switch (project.getService()) {

		case "MANDAYS":
			LOGGER.debug("Invoking Mandays Type..");
			resource = buildMandaysService(project);
			break;

		case "TRAINING":

			break;

		case "INSTALLATION":

			break;

		case "PURCHASE_ORDER":

			break;

		default:
			break;
		}

		
		
		return resource;
	}
	
	protected ByteArrayResource buildMandaysService(Project project) throws Exception{
		String excelFilePath = "/Users/kukuhargaditya/Documents/workspace/tpv_template/tpv_template_1.0.xlsx";
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
		Workbook workbook = WorkbookFactory.create(inputStream);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		generateSummarySheet(project, workbook);
		MandaysService service = mandaysRepository.findFirstByProjectId(project.getId());
		LOGGER.debug("service: "+service+",project: "+project.getId());
		generateScopeOfServiceSheet(service, workbook);
		generateResourceAllocationSheet(service, workbook);
		XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
		workbook.write(stream);
		workbook.close();
		return new ByteArrayResource(stream.toByteArray());
	}

	private void generateResourceAllocationSheet(MandaysService service, Workbook workbook) {
		Sheet sheet = getWorksheet(workbook,2);
		generateResourceAllocation(sheet, service);
		
	}

	private Sheet getWorksheet(Workbook workbook, int index) {
		Sheet sheet = workbook.getSheetAt(index);
		return sheet;
	}

	private void generateSummarySheet(Project project, Workbook workbook) {
		Sheet sheet = getWorksheet(workbook, 0);
		generateSummarySheetContent(sheet, project);
	}
	
	private void generateScopeOfServiceSheet(MandaysService service, Workbook workbook) {
		Sheet sheet = getWorksheet(workbook, 1);
		generateScopeOfServices(sheet, service);
	}

	private String getTrimmed(String text) {
		return (text != null) ? text.trim() : null;
	}
	
	private void generateSummarySheetContent(Sheet sheet, Project project) {
		int rowCount = 5;
		int colCount = 10;
		Row row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getCode());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getClientName());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getDepartment());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getName());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getProjectGroup());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(userService.getFullName(project.getSales()));
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(userService.getFullName(project.getSalesLead()));
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(userService.getFullName(project.getPresales1()));
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(userService.getFullName(project.getPresales2()));
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(userService.getFullName(project.getPmoDelivery()));
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(getTrimmed(project.getDescription()));
		row.getCell(colCount).getCellStyle().setWrapText(true);
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getPoCurrency());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getProjectType());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getPoNumber());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(getLeadStage(project.getLeadStage()));
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getWeekDuration());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getCloseDate());
		row = sheet.getRow(rowCount++);
		row.getCell(colCount).setCellValue(project.getKickOffDate());
//		row = sheet.getRow(rowCount++);
//		row.getCell(colCount).setCellValue(project.getStartDate());
//		row = sheet.getRow(rowCount++);
//		row.getCell(colCount).setCellValue(project.getFinishDate());
		

	}
	
	private void writeDownScopes(Sheet sheet,Collection<String> inScopes,int maxRow,int rowCount) {
		if (inScopes == null) return;
		int colCount = 3;
		int counter = 0;
		Iterator<String>it = inScopes.iterator();
		while(it.hasNext() && counter++ < maxRow) {
			Row row = sheet.getRow(rowCount++);
			row.getCell(colCount).setCellValue(it.next());
		}
	}
	
	private void writeDownDeliverables(Sheet sheet,List<DeliverableMap> list,int maxRow,int rowCount) {
		if(list == null) return;
		int colCount = 3;
		int counter = 0;
		Iterator<DeliverableMap>it = list.iterator();
		while(it.hasNext() && counter++ < maxRow) {
			Row row = sheet.getRow(rowCount++);
			DeliverableMap map = it.next();
			row.getCell(colCount).setCellValue(map.getName());
			row.getCell(colCount + 11).setCellValue(map.getQuantity());
			row.getCell(colCount + 12).setCellValue(map.getUom());
			row.getCell(colCount + 13).setCellValue(map.getRemarks());
		}
	}
	
	private void writeDownMilestones(Sheet sheet,List<MilestoneMap> list,int maxRow,int rowCount) {
		if (list == null) return;
		int colCount = 2;
		int counter = 0;
		Iterator<MilestoneMap>it = list.iterator();
		while(it.hasNext() && counter++ < maxRow) {
			Row row = sheet.getRow(rowCount++);
			MilestoneMap ms = it.next();
			row.getCell(colCount - 1).setCellValue(counter);
			row.getCell(colCount).setCellValue(ms.getMilestoneName());
			row.getCell(colCount + 8).setCellValue(ms.getWeek());
		}
	}
	
	private void generateScopeOfServices(Sheet sheet, MandaysService service) {
		
		writeDownScopes(sheet, service.getInScopes(), 5, 5);
		writeDownDeliverables(sheet, service.getDeliverables(), 6, 14);
		writeDownScopes(sheet, service.getOutScopes(), 5, 22);
		writeDownMilestones(sheet, service.getMilestones(), 12, 32);
		

	}
	
	private void generateResourceAllocation(Sheet sheet, MandaysService service) {
		writeDownActivities(sheet,service.getActivities(),65,31);
		writeDownMandaysAllocation(sheet,service.getPersonnels(),65,27);
		

	}
	
	private void writeDownMandaysAllocation(Sheet sheet, List<PersonnelMap> personnels, int maxRow, int rowCount) {
		
		int counter = 0,colCount = 111;
		Iterator<PersonnelMap>it = personnels.iterator();
		
		while(it.hasNext() && counter++ < maxRow) {
			PersonnelMap personel = it.next();
			LOGGER.info("personnel-"+counter+": "+personel.toString()+"=="+rowCount+","+colCount);
			Row row = sheet.getRow(rowCount);
			row.getCell(colCount).setCellValue(personel.getRoleNm());
			row = sheet.getRow(rowCount + 1);
			row.getCell(colCount).setCellValue(personel.getRscLevel());
			row = sheet.getRow(rowCount + 2);
			row.getCell(colCount).setCellValue(personel.getCount());
			colCount++;
		}
		
	}

	private void writeDownActivities(Sheet sheet,List<ActivityMap> activities,int maxRow,int rowCount) {
		int colCount = 2,counter = 0;
		Iterator<ActivityMap>it = activities.iterator();
		while(it.hasNext() && counter++ < maxRow) {
			Row row = sheet.getRow(rowCount++);
			ActivityMap act = it.next();
			row.getCell(colCount - 1).setCellValue(counter);
			row.getCell(colCount).setCellValue(act.getGroup());
			row.getCell(colCount + 1).setCellValue(act.getName());
			row.getCell(colCount + 2).setCellValue(act.getDuration());
		}
		
	}

	private CellStyle getDateStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat((short) 15); // 0xf, "d-mmm-yy"
		return style;
	}
	
	private CellStyle getDescriptionStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		return style;
	}

	private CellStyle getSummaryStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat((short) 15); // 0xf, "d-mmm-yy"
		// style.set
		return style;
	}

	private CellStyle getAmountStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		style.setDataFormat((short) 4); // "#,##0.00"
		return style;
	}

	// private void

	private CellStyle getRightStyle(Workbook workbook) {
		CellStyle style = workbook.createCellStyle();
		// style.setDataFormat((short) 4); // "#,##0.00"
		style.setBorderRight(BorderStyle.THICK);
		return style;
	}

	private CellStyle getHeaderStyle(Workbook workbook) {
		// create style for header cells
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setFontName("Arial");
		style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
		style.setFont(font);
		return style;
	}

}
