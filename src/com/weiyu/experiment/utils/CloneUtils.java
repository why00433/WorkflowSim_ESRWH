package com.weiyu.experiment.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weiyu.experiment.domain.Event;

public class CloneUtils {

	/**
	 * 深拷贝一个Map对象
	 * @param schedules
	 * @return
	 */
	public static Map<Integer, List<Event>> clone(Map<Integer, List<Event>> schedules){
		Map<Integer, List<Event>> map = new HashMap<>();
		for(Map.Entry<Integer, List<Event>> entry : schedules.entrySet()){
			int vmId = entry.getKey();
			List<Event> events = entry.getValue();
			List<Event> newEvents = new ArrayList<>();
			for(Event event : events){
				newEvents.add(event.clone());
			}
			map.put(vmId, newEvents);
		}
		return map;
	}
}
