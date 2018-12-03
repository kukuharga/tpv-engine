package com.nuvola.tpv.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.nuvola.tpv.model.InvoiceDistribution;
import com.nuvola.tpv.model.Menu;
import com.nuvola.tpv.model.Role;
import com.nuvola.tpv.model.SubMenu;
import com.nuvola.tpv.repo.MenuRepository;
import com.nuvola.tpv.repo.RoleRepository;
import com.nuvola.tpv.repo.UserRepository;


@Component
public class MenuService {
	private static Log log = LogFactory.getLog(MenuService.class);
	@Autowired
	private MenuRepository menuRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	
	public Collection<Menu> getMenuByUser(String userName) {
		log.info("==username==" + userName);
		Set<String>menusInString = getAllowedMenusInString(userName);
		log.debug("menusInString=="+menusInString);
//		Collection<Menu> menus = (Collection<Menu>) menuRepository.findAllById(menusInString);
		Collection<Menu> menus = getMenuStructure(menusInString);
//		menus.addAll(subMenus);
//		subMenus.clear();
		return menus;
	}
	
	/**
	 * Get allowed menu of a specified user
	 * @param userName
	 * @return list of string
	 */
	private Set<String> getAllowedMenusInString(String userName) {
		Iterable<String> rolesInString = userRepository.findById(userName).get().getRoles();
		Iterable<Role> roles = roleRepository.findAllById(rolesInString);
		Set<String> menusInString = new HashSet<String>();
		roles.forEach((role) -> menusInString.addAll(role.getAllowedMenus()));
		return menusInString;
	}
	private boolean isParentMenu(String menuName) {
		return '_' == menuName.charAt(1);
	}
	
	private boolean isSubMenu(String menuName) {
		return !isParentMenu(menuName);
	}

	public List<Menu> getMenuStructure(Collection<String> menusInString) {
		List<Menu> menus = menuRepository.findAll();

		for (Iterator<Menu> mainIterator = menus.iterator(); mainIterator.hasNext();) {
			Menu mainMenu = mainIterator.next();
			boolean found = false;

			for (String o : menusInString) {

				if (isParentMenu(o) && o.equals(mainMenu.getCode())) found = true;

			}

			if (mainMenu.getSubMenu() != null) {
				for (Iterator<SubMenu> subIterator = mainMenu.getSubMenu().iterator(); subIterator.hasNext();) {
					boolean subFound = false;
					SubMenu subMenu = subIterator.next();
					for (String o : menusInString) {
						if (isSubMenu(o) && o.equals(subMenu.getCode())) {
//							log.debug("submenu found!==" + subMenu.getCode());
							found = true;
							subFound = true;
						}
					}
					if (!subFound) subIterator.remove();

				}
			}
			if (!found) mainIterator.remove();

		}

		return menus;
	}
	

}
