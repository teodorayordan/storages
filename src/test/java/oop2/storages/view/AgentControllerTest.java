package oop2.storages.view;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import oop2.storages.Agent;
import oop2.storages.Storage;

public class AgentControllerTest {
	
	private static Agent agent1 = new Agent();
	private static Agent agent2 = new Agent();
	private static Agent agent3 = new Agent();
	private static List<Agent> agentList = new ArrayList<Agent>();
	private static Storage storage = new Storage();
	
	@BeforeClass
	public static void init() {
		agentList.add(agent1);
		agentList.add(agent2);
		agentList.add(agent3);
		for (Agent agent : agentList) {
			agent.setRating(3);
		}
		
		storage.setAgentList(agentList);
	}
	
	@Test
	public void testCalculateRating() {
		double result = AgentController.calculateRating(5);
		assertEquals("Test Failed", result, 0.33, 0);
		
		double result2 = AgentController.calculateRating(3);
		assertEquals("Test Failed2", result2, 0.33, 0);
		
		double result3 = AgentController.calculateRating(6);
		assertEquals("Test Failed3", result3, 0.25, 0);
	}
	
	@Test
	public void testChangeRating() {
		AgentController.changeRating(storage, agent1);
		assertEquals("Test Failed", agent1.getRating(), 3.5, 0);
		assertEquals("Test Failed2", agent2.getRating(), 2.67, 0);
	}
}
