/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsehtmlmysitecost;

import java.sql.*;
/**
 *
 * @author spidchenko.d
 */
public class DBConnection {
        //localhost SQL server:
    private static final String URL = "jdbc:mysql://localhost:3306/site_crowler";
    private static final String USER = "root";
    private static final String PASS = "root";
    
    private static Connection con;
 
    private static Statement updateStmt;
    private static Statement stmt;
    private static ResultSet rs;

    private static int rowUpdatedCounter = 0; 
    
    public int getNumSitesToParse(){
        
        String getNumSitesToParseQuery = "SELECT COUNT(domains.id) FROM domains LEFT JOIN analyses ON domains.id = analyses.domain_id WHERE domain_id IS null";
        
        int returnNum = 0;
        
        try{
            con = DriverManager.getConnection(URL, USER, PASS);
            stmt = con.createStatement();
            rs = stmt.executeQuery(getNumSitesToParseQuery);
            rs.next();  //Возьмем первую и единственную запись
            returnNum = rs.getInt("COUNT(domains.id)");
            
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            
        }
        return returnNum;
    }
    
    
    
    public void setNewDomain(String domainName, String domainCost){
        
        String setNewDomainQuery = "INSERT INTO domains (`name`, `cost`) VALUES (\""+domainName+"\", \""+domainCost+"\")";
        
        try{
            con = DriverManager.getConnection(URL, USER, PASS);


            updateStmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            
            //System.err.println(setNewDomainQuery);
            updateStmt.executeUpdate(setNewDomainQuery);
                          
        
        } catch (SQLException sqlEx) {
            if(sqlEx.toString().contains("Duplicate entry")){
                System.err.println("Duplicate: "+domainName);
            } else{
                sqlEx.printStackTrace();    
            }
            
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { updateStmt.close(); } catch(SQLException se) { /*can't do anything */ }            
//            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
//            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
    }
    
    public void initNextSiteToParse(Site siteToInit){
        
        String getNextSiteQuery = "SELECT domains.name, domains.id FROM domains LEFT JOIN analyses ON domains.id = analyses.domain_id WHERE domain_id IS null LIMIT 1";
        
        try{
            con = DriverManager.getConnection(URL, USER, PASS);
            stmt = con.createStatement();
            rs = stmt.executeQuery(getNextSiteQuery);
            rs.next();  //Возьмем первую и единственную запись
            siteToInit.setdomainName(rs.getString("name"));
            siteToInit.setId(rs.getString("id"));                          
        
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } finally {
            //close connection here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
        }                 
    }
   
    public void writeSiteToDatabase(Site siteToWrite){
        
        int isInDMOZ = (siteToWrite.getIsInDMOZ().contains("Да"))?1:0;
        int isInYACA = (siteToWrite.getIsInYACA().contains("Да"))?1:0;
        int isHaveAdSense = (siteToWrite.getIsHaveAdSense().contains("Да"))?1:0;
        int isHaveYaDirect =(siteToWrite.getIsHaveYaDirect().contains("Да"))?1:0;
        
        String setNewAnalysisQuery = "INSERT INTO `analyses` (`domain_id`, `date`, `cost`, `views_per_day`, `hosts_per_day`, `income_per_day`, `income_per_month`, `adsence`, `yadirect`, `domain_registered`, `domain_expires`, `alexa_rate`, `tyc`, `is_in_dmoz`, `in_in_yaca`, `ip`)\n" +
"VALUES (\""+siteToWrite.getId()+"\", \""+siteToWrite.getLastCheck()+"\", \""+siteToWrite.getLastCost()+"\", \""+siteToWrite.getViewsPerDay()+"\", \""+siteToWrite.getHostsPerDay()+"\", \""+siteToWrite.getIncomePerDay()+"\", \""+siteToWrite.getIncomePerMonth()+"\", \""+isHaveAdSense+"\", \""+isHaveYaDirect+"\", \""+siteToWrite.getDomainRegistered()+"\", \""+siteToWrite.getDomainExpires()+"\", \""+siteToWrite.getAlexaRate()+"\", \""+siteToWrite.getTyc()+"\", \""+isInDMOZ+"\", \""+isInYACA+"\", \""+siteToWrite.getIp()+"\")";
                

//(\""+domainName+"\", \""+domainCost+"\")";
        
        try{
            con = DriverManager.getConnection(URL, USER, PASS);


            updateStmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            
//    System.out.println(setNewAnalysisQuery);
            updateStmt.executeUpdate(setNewAnalysisQuery);
                          
        
        } catch (SQLException sqlEx) {
            if(sqlEx.toString().contains("Duplicate entry")){
                System.err.println("Duplicate? ");
            } else{
                sqlEx.printStackTrace();    
            }
            
        } finally {
            //close connection ,stmt and resultset here
            try { con.close(); } catch(SQLException se) { /*can't do anything */ }
            try { updateStmt.close(); } catch(SQLException se) { /*can't do anything */ }            
//            try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
//            try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
        }
        
    }
}
