package main.java.domain.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import main.java.domain.model.StatisticGeneral;
import main.java.domain.model.StatisticUser;


@ApplicationScoped
@Transactional
@Default

public class StatisticServiceImpl implements StatisticService {

	@PersistenceContext(unitName="StatisticPU")
	private EntityManager em;
	
	
	@Override
	public List<StatisticGeneral> getGeneralStats(String itemId) {		//retourne les stats générales de l'item sélectionné
		return em.createQuery(	"SELECT s FROM statisticGeneral s WHERE s.Item_ID = :id", StatisticGeneral.class).setParameter("id", itemId).getResultList() ;
	}
	
	@Override
	public List<StatisticUser> getItemStats(String itemId) {		//retourne les stats de l'item sélectionné pour chaque utilisateur
		return em.createQuery(	"SELECT s FROM statisticUser s WHERE s.Item_ID = :itemid", StatisticUser.class).setParameter("itemid", itemId).getResultList();
	}

	@Override
	public List<StatisticUser> getUserStats(String usrId, String itemId) {		//retourne les stats de l'utilisateur donné pour l'item sélectionné
		return em.createQuery(	"SELECT s FROM statisticUser s WHERE s.User_ID = :usrid AND s.Item_ID = :itemid", StatisticUser.class).setParameter("usrid", usrId).setParameter("itemid", itemId).getResultList();
	}
	
	@Override
	public List<StatisticUser> getAllUser() {
		return em.createQuery("SELECT s FROM statisticUser s", StatisticUser.class).getResultList();
	}
	
	@Override
	public List<StatisticGeneral> getAllGeneral() {
		return em.createQuery("SELECT s FROM statisticGeneral s", StatisticGeneral.class).getResultList();
	}

	@Override
	public void addUserStats(StatisticUser stats) {
		em.persist(stats);
	}
	
	@Override
	public void addGeneralStats(StatisticGeneral stats) {
		em.persist(stats);
	}
	
	
	@Override
	public void clickOnItem(String usrId, String itemId) {		//événement de clic sur un item par l'utilisateur donné : incrémente le nombre de clics par cet utilisateur sur l'item sélectionné ainsi que sur la catégorie correspondante
		Query q = em.createQuery(	"UPDATE statisticUser SET nClics_Item = nClics_Item+1, nClics_Categorie = nClics_Categorie+1 WHERE Item_ID :itemid AND User_ID = :usrid") ;
		Query q2 = em.createQuery(	"UPDATE statisticGeneral SET nClics_Item = nClics_Item+1 WHERE Item_ID = :itemid") ;
		Query q3 = em.createQuery(	"UPDATE statisticGeneral SET nClics_Categorie = nClics_Categorie+1 WHERE Categorie = (SELECT Categorie FROM statisticGeneral WHERE Item_ID = :itemid)") ;
		q.setParameter("itemid", itemId).setParameter("usrid", usrId).executeUpdate();
		q2.setParameter("itemid", itemId).executeUpdate();
		q3.setParameter("itemid", itemId).executeUpdate();
	}
	
	@Override
	public void clickOnItem(String itemId) {		//événement de clic sur un item par un utilisateur quelconque : incrémente le nombre de clics sur l'item sélectionné ainsi que sur la catégorie correspondante
		Query q = em.createQuery(	"UPDATE statisticGeneral SET nClics_Item = nClics_Item+1, nClics_Categorie = nClics_Categorie+1 WHERE Item_ID = :itemid") ;
		q.setParameter("itemid", itemId).executeUpdate();
	}
	
	@Override
	public void research(String usrId, String word) {		//événement de recherche d'un mot par l'utilisateur donné
		Query q = em.createQuery(	"UPDATE statisticUser SET nClics_Mot = nClics_Mot+1 WHERE User_ID = :usrid AND Mot = :mot") ;
		Query q2 = em.createQuery(	"UPDATE statisticGeneral SET nClics_Mot = nClics_Mot+1 WHERE Mot = :mot") ;
		Query q3 = em.createQuery(	"SELECT nClics_Mot FROM statisticUser WHERE User_ID = :usrid AND Mot = :mot)", Long.class) ;
		Query q4 = em.createQuery(	"SELECT nClics_Mot FROM statisticGeneral WHERE Mot = :mot)", Long.class) ;
		
		q.setParameter("usrid", usrId).setParameter("mot", word).executeUpdate();
		q2.setParameter("mot", word).executeUpdate();
		Long nClics = (Long) q3.setParameter("usrid", usrId).setParameter("mot", word).getSingleResult();
		Long nClicsGen = (Long) q4.setParameter("mot", word).getSingleResult();
		Long maxClics = em.createQuery(	"SELECT MAX(nClics_Mot) FROM statisticUser", Long.class).getSingleResult() ;
		Long maxClicsGen = em.createQuery(	"SELECT MAX(nClics_Mot) FROM statisticGeneral", Long.class).getSingleResult() ;
		
		if (nClics > maxClics)
			em.createQuery(	"UPDATE statisticUser SET Mot = (SELECT Mot FROM statisticUser WHERE nClics_Mot = :nclics)").setParameter("nclics", nClics).executeUpdate();
		if (nClicsGen > maxClicsGen)
			em.createQuery(	"UPDATE statisticGeneral SET Mot = (SELECT Mot FROM statisticGeneral WHERE nClics_Mot = :nclicsgen)").setParameter("nclicsgen", nClicsGen).executeUpdate() ;
		
	}
	
	@Override
	public void research(String word) {		//événement de recherche d'un mot
		Query q2 = em.createQuery(	"UPDATE statisticGeneral SET nClics_Mot = nClics_Mot+1 WHERE Mot = :mot") ;
		Query q4 = em.createQuery(	"SELECT nClics_Mot FROM statisticGeneral WHERE Mot = :mot)", Long.class) ;
		
		q2.setParameter("mot", word).executeUpdate();
		Long nClicsGen = (Long) q4.setParameter("mot", word).getSingleResult();
		Long maxClicsGen = em.createQuery(	"SELECT MAX(nClics_Mot) FROM statisticGeneral", Long.class).getSingleResult() ;
		
		if (nClicsGen > maxClicsGen)
			em.createQuery(	"UPDATE statisticGeneral SET Mot = (SELECT Mot FROM statisticGeneral WHERE nClics_Mot = :nclicsgen)").setParameter("nclicsgen", nClicsGen).executeUpdate() ;
		
	}


	@Override
	public void removeUserStats(String usrId, String itemId) {
		em.createQuery(	"DELETE FROM statisticUser WHERE UserID = :usrid AND ItemID = :itemid").setParameter("usrid", usrId).setParameter("itemid", itemId).executeUpdate();
	}

	@Override
	public void removeGeneralStats(String itemId) {
		em.createQuery(	"DELETE FROM statisticGeneral WHERE ItemID = :itemid").setParameter("itemid", itemId).executeUpdate();
	}

	

}
