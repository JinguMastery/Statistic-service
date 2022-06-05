package main.java.api;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import main.java.domain.model.StatisticGeneral;
import main.java.domain.model.StatisticGeneral.Categorie;
import main.java.domain.model.StatisticUser;
import main.java.domain.service.StatisticService;


@ApplicationScoped
@Transactional
@Path("/statistic")
public class StatisticRestService {
	
	@Inject 
	private StatisticService statsService;
	
	public void setStatisticservice(StatisticService serv) {
		statsService = serv;
	}
	
	@GET
	@Path("/getuserstats")
	@Produces("text/plain")
	public String getUserStatsBySearch(@QueryParam("userid") String usrId, @QueryParam("itemid") String itemId) {
		try {
			StatisticUser stats = statsService.getUserStats(usrId, itemId);
			return toStreamUser(stats);
		} catch (NoResultException exc) {
			return "Error : there is no user with id " + usrId ;
		}
	}
	
	@GET
	@Path("/getgeneralstats")
	@Produces("text/plain")
	public String getItemStatsBySearch(@QueryParam("itemid") String itemId) {
		List<StatisticGeneral> stats = statsService.getGeneralStats(itemId);
		if (!stats.isEmpty())
			return toStreamGeneral(stats);
		else
			return "Error : there is no item with id " + itemId ;
	}
		
	
	@GET
	@Path("/alluserstats")
	@Produces("text/plain")
	public String getAllUserStats() {
		List<StatisticUser> all = statsService.getAllUser();
		return toStreamUser(all);
	}
	
	@GET
	@Path("/allgeneralstats")
	@Produces("text/plain")
	public String getAll() {
		List<StatisticGeneral> all = statsService.getAllGeneral();
		return toStreamGeneral(all);
	}
	
	@GET
	@Path("/adduserstatstest")
	@Produces("text/plain")
	public String addUserStatsTest() {
		StatisticUser stats = new StatisticUser("u123", "i123", 1, "cours", 100, Categorie.Livres, 150);
		statsService.addUserStats(stats);
		return "You inserted " + stats.toString();
	}
	
	@GET
	@Path("/addgeneralstatstest")
	@Produces("text/plain")
	public String addGeneralStatsTest() {
		StatisticGeneral stats = new StatisticGeneral("i123", 10, "cours", 1000, Categorie.Livres, 1500);
		statsService.addGeneralStats(stats);
		return "You inserted " + stats.toString() ;
	}
	
	@GET
	@Path("/adduserstats")
	@Produces("text/plain")
	public String addUserStats(@QueryParam("userid") String userId, @QueryParam("itemid") String itemId) {
		StatisticUser stats = new StatisticUser(userId, itemId, 1, "cours", 100, Categorie.Livres, 150) ;
		statsService.addUserStats(stats);
		return "You inserted " + stats.toString();
	}
	
	@GET
	@Path("/addgeneralstats")
	@Produces("text/plain")
	public String addGeneralStats(@QueryParam("itemid") String itemId) {
		StatisticGeneral stats = new StatisticGeneral(itemId, 10, "cours", 1000, Categorie.Livres, 1500) ;
		statsService.addGeneralStats(stats);
		return "You inserted " + stats.toString() ;
	}
	
	@GET
	@Path("/deluserstats")
	@Produces("text/plain")
	public String deleteUserStats(@QueryParam("userid") String userId, @QueryParam("itemid") String itemId) {
		
		List<StatisticUser> stats = statsService.getUserStats(userId, itemId);
		if(stats.isEmpty()) {
			return "Error : there is no user with id " + userId ;
		}
		else {
			statsService.removeUserStats(userId, itemId);
			return "You deleted statistic from user " + userId + " about item " + itemId ;	
		}
		
	}
	
	@GET
	@Path("/delgeneralstats")
	@Produces("text/plain")
	public String deleteUserStats(@QueryParam("itemid") String itemId) {
		
		List<StatisticGeneral> stats = statsService.getGeneralStats(itemId);
		if(stats.isEmpty()) {
			return "Error : there is no user with id " + itemId ;
		}
		else {
			statsService.removeGeneralStats(itemId);
			return "You deleted general statistic from item " + itemId ;	
		}
		
	}
	
	public String toStreamUser(List<StatisticUser> userStats) {
		return userStats.stream().map(StatisticUser::toString).collect(Collectors.joining("\n"));
	}
	
	public String toStreamGeneral(List<StatisticGeneral> itemStats) {
		return itemStats.stream().map(StatisticGeneral::toString).collect(Collectors.joining("\n"));
	}
	
}
