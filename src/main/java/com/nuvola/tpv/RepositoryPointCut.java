package com.nuvola.tpv;

import java.util.List;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nuvola.tpv.model.Project;
import com.nuvola.tpv.service.ProjectService;

@Aspect
@Component
public class RepositoryPointCut {
	@Autowired
	ProjectService projectService;

	@Pointcut(value = "execution(* com.nuvola.tpv.repo.ProjectRepository.find*(..))")
	public void after() {
		System.out.println("====++++after execution of {1}");
	}
	
	@Pointcut(value = "execution(* org.springframework.data.repository.CrudRepository.findById(..))")
	public void afterRepo() {
		System.out.println("====++++after find by Id of {1}");
	}
	
	@Pointcut("within(@org.springframework.stereotype.Repository *)")
	public void repositoryClassMethods() {}
	
	@Around("repositoryClassMethods()")
	public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println("====++++after execution of {2}");
		return pjp.proceed();
	}
	
	@SuppressWarnings("unchecked")
	@Around("after()")
	public Object testMethod(ProceedingJoinPoint jp) throws Throwable {
		System.out.println("====++++testMethod {1}"+jp.proceed().getClass());
//		projectService.injectFinancialInformation((List<Project>) jp.proceed());
		List<Project> projects = projectService.injectFinancialInformation((List<Project>) jp.proceed());
		return projects;
	}
	
	@SuppressWarnings("unchecked")
	@Around("afterRepo()")
	public Object testMethod1(ProceedingJoinPoint jp) throws Throwable {
		System.out.println("====++++testMethodAfterRepo {1}"+jp.proceed().getClass());
		Optional optional  = (Optional) jp.proceed();
	
		return jp.proceed();
	}
}
