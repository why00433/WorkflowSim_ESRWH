package com.weiyu.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weiyu.experiment.domain.Event;
import com.weiyu.experiment.utils.CloneUtils;

class Person implements Cloneable {
	int schoolId;
	String name;
	double score;

	public Person() {
	}

	public Person(int schoolId, String name, double score) {
		this.schoolId = schoolId;
		this.name = name;
		this.score = score;
	}

	public int getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(int schoolId) {
		this.schoolId = schoolId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	protected Person clone() throws CloneNotSupportedException {
		Person person = null;
		try {
			super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return person;
	}

	@Override
	public String toString() {
		return "Person [schoolId=" + schoolId + ", name=" + name + ", score=" + score + "]";
	}

}

public class HashMapTest {

	public static void main(String[] args) {
		/*Map<Integer, Person> persons = new HashMap<>();
		Person person1 = new Person(1, "Sam", 78);
		Person person2 = new Person(2, "Lori", 80);
		persons.put(1, person1);
		persons.put(2, person2);

		Map<Integer, Person> personsNew = new HashMap<>();
		personsNew.putAll(persons);

		Person person = personsNew.get(1);
		person.setName("Samuel");

		System.out.println("Original Map");
		printMap(persons);
		System.out.println("New Map");
		printMap(personsNew);*/
		
		
		Map<Integer, List<Event>> map = new HashMap<>();
		Event event1 = new Event(1.2, 3.2);
		Event event2 = new Event(12.3, 15.3);
		List<Event> events = new ArrayList<>();
		events.add(event1);
		events.add(event2);
		
		map.put(1, events);
		
		Map<Integer, List<Event>> clonedMap = CloneUtils.clone(map);
		List<Event> list = clonedMap.get(1);
		Event newEvent1 = list.get(0);
		newEvent1.finish = 333.3;
		newEvent1.start = 222.2;
		
		printEventMap(map);
		printEventMap(clonedMap);
		
	}

	public static void printMap(Map<Integer, Person> map) {
		for (Map.Entry<Integer, Person> entry : map.entrySet()) {
			System.out.println(entry.getValue());
		}
	}
	
	public static void printEventMap(Map<Integer, List<Event>> map) {
		for (Map.Entry<Integer, List<Event>> entry : map.entrySet()) {
			List<Event> events = entry.getValue();
			System.out.println(events);
		}
	}

}
