package com.nuvola.tpv.model;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

public @Data class ActivityMap {
	private String name;
	private String group;
	private int duration;
	private List<ResourceMandays> resourceAllocations;
	private int startDay;

	public int getActivityMandays() {
		if (resourceAllocations == null)
			return 0;
		return resourceAllocations.stream().collect(Collectors.summingInt(n -> n.getMandays()));

	}
	
	public int getFinishDay() {
		return this.startDay + this.duration - 1;
	}
}
