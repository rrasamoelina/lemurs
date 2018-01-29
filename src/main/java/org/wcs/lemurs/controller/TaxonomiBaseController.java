/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wcs.lemurs.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import static org.wcs.lemurs.controller.BaseController.ROLE_ADMINISTRATEUR;
import static org.wcs.lemurs.controller.BaseController.ROLE_EXPERT;
import static org.wcs.lemurs.controller.BaseController.ROLE_MODERATEUR;
import org.wcs.lemurs.model.TaxonomiBase;
import org.wcs.lemurs.model.Utilisateur;
import org.wcs.lemurs.modele_vue.VueRechercheTaxonomi;
import org.wcs.lemurs.service.TaxonomiBaseService;
import org.wcs.lemurs.util.UploadFile;

/**
 *
 * @author rudyr
 */
@RestController
public class TaxonomiBaseController {

    @Autowired(required = true)
    @Qualifier("taxonomiBaseService")
    private TaxonomiBaseService taxonomiBaseService;

    @RequestMapping(value = "/findByespeceTaxo", method = RequestMethod.POST, headers = "Accept=application/json")
    public List<VueRechercheTaxonomi> findByespece(@RequestBody VueRechercheTaxonomi t) throws Exception {
        return (List<VueRechercheTaxonomi>)(List<?>)taxonomiBaseService.findMultiCritere(t);
    }

//    @RequestMapping(value = "/detailLemurien", method = RequestMethod.POST, headers = "Accept=application/json")
//    public TaxonomiBase detailLemurien(@RequestParam(value = "idLemurien") int id) throws Exception {
//        TaxonomiBase val = new TaxonomiBase();
//        val.setId(id);
//        taxonomiBaseService.findById(id);
//        return taxonomiBaseService.findMultiCritere(t);
//    }
    @RequestMapping(value = "/getallTaxo", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<TaxonomiBase> getall() throws Exception {
        List<TaxonomiBase> resultat = taxonomiBaseService.findAll();
        return resultat;
    }

    @RequestMapping(value = "/saveTaxo", method = RequestMethod.POST, headers = "Accept=application/json")
    public void saveOrupdate(HttpSession session, @RequestBody TaxonomiBase t) throws Exception {
        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (taxonomiBaseService.checkRole(u, ROLE_MODERATEUR)) {
            taxonomiBaseService.save(t);
        }
    }

    @RequestMapping(value = "/deleteTaxo", method = RequestMethod.POST, headers = "Accept=application/json")
    public void delete(HttpSession session, @RequestBody TaxonomiBase t) throws Exception {
        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (taxonomiBaseService.checkRole(u, ROLE_MODERATEUR)) {
            taxonomiBaseService.delete(t);
        }
    }

    // temp    
    @RequestMapping(value = "/getFamille", method = RequestMethod.POST, headers = "Accept=application/json")
    public List<String> getFamily() throws Exception {
        return (List<String>) (List<?>) taxonomiBaseService.executeSqlList("select distinct dwcfamily from taxonomi_base");
    }

    @RequestMapping(value = "/getGenre", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<String> getGenre(@RequestParam(value = "famille") String requestData) throws Exception {
        List<String> nom = new ArrayList<>();
        nom.add("dwcf");
        List<Object> param = new ArrayList<>();
        param.add(requestData);
        return (List<String>) (List<?>) taxonomiBaseService.executeSqlList("select distinct genus from taxonomi_base where dwcfamily = :dwcf", nom, param);
    }

    @RequestMapping(value = "/getEspece", method = RequestMethod.GET, headers = "Accept=application/json")
    public List<String> getEspece(@RequestParam(value = "genre") String requestData) throws Exception {
        List<String> nom = new ArrayList<>();
        nom.add("dwcgen");
        List<Object> param = new ArrayList<>();
        param.add(requestData);
        return (List<String>) (List<?>) taxonomiBaseService.executeSqlList("select distinct scientificname from taxonomi_base where genus = :dwcgen", nom, param);
    }

    @RequestMapping(value = "/assigner", method = RequestMethod.POST, headers = "Accept=application/json")
    public ModelAndView validerAll(HttpSession session, @RequestParam(value = "valeur[]") String[] valeur, @RequestParam(value = "idExpert") int idExpert) throws Exception {
        Utilisateur utilisateur = (Utilisateur)session.getAttribute("utilisateur");
        if(!taxonomiBaseService.checkRole(utilisateur, ROLE_ADMINISTRATEUR)) return new ModelAndView("redirect:login");
        Utilisateur exp = new Utilisateur();
        exp.setId(idExpert);
        taxonomiBaseService.findById(exp);
        if(taxonomiBaseService.checkRole(exp, ROLE_EXPERT))taxonomiBaseService.checkFamille(valeur, idExpert);
        ModelAndView valiny = new ModelAndView("redirect:detailUtilisateur?idUtilisateur="+idExpert);
        return valiny;
    }

    @RequestMapping(value = "/taxonomiCsv")
    public void taxonomiCsv(HttpSession session, HttpServletResponse response) throws Exception {
        List<TaxonomiBase> liste = taxonomiBaseService.findAll();
        response.setHeader("Content-Type", "text/csv");
        response.setHeader("Content-Disposition", "attachment;filename=\"taxonomi.csv\"");
        UploadFile upf = new UploadFile();
        upf.writeCsv(liste, ';', response.getOutputStream());                
    }
    
    @RequestMapping(value = "/getDetailTaxo")
    public ModelAndView darwinportal(HttpSession session, @RequestParam("id") Integer id) {
        ModelAndView valiny = new ModelAndView("page-detail");
        
        TaxonomiBase taxo = new TaxonomiBase();
        taxo.setId(id);
        try {
            taxonomiBaseService.findById(taxo);            
        } catch (Exception ex) {
            Logger.getLogger(DarwinCoreController.class.getName()).log(Level.SEVERE, null, ex);
        }
        valiny.addObject("taxo", taxo);        
        return valiny;
    }
}
