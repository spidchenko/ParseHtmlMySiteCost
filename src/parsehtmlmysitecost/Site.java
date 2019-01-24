/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsehtmlmysitecost;

/**
 *
 * @author spidchenko.d
 * 
 * id	int(6) Автоматическое приращение	 
domain_id	int(6)	 
date	datetime	 
cost	double	 
views_per_day	int(11)	 
hosts_per_day	int(11)	 
income_per_day	double	 
income_per_month	double	 
adsence	smallint(1)	 
yadirect	smallint(1)	 
domain_registered	datetime	 
domain_expires	datetime	 
alexa_rate	int(11)	 
tyc	int(11)	 
is_in_dmoz	smallint(1)	 
in_in_yaca	smallint(1)	 
ip
 */
public class Site {
    private String domainName ="";
    private String id ="-1";
    private String lastCheck ="1900-01-01";
    private String lastCost ="-1";
    private String viewsPerDay ="-1";
    private String hostsPerDay ="-1";
    private String incomePerDay ="-1";
    private String incomePerMonth ="-1";
    private String isHaveAdSense ="-1";
    private String isHaveYaDirect ="-1";
    private String domainRegistered ="1900-01-01";
    private String domainExpires ="1900-01-01";
    private String alexaRate ="-1";
    private String tyc ="-1";
    private String isInDMOZ ="-1";
    private String isInYACA ="-1";
    private String ip ="-1";

    public void setdomainName(String domainName) {
        this.domainName = domainName;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public void setLastCheck(String lastCheck) {
        this.lastCheck = lastCheck;
    }

    public void setLastCost(String lastCost) {
        this.lastCost = lastCost;
    }

    public void setViewsPerDay(String viewsPerDay) {
        this.viewsPerDay = viewsPerDay;
    }

    public void setHostsPerDay(String hostsPerDay) {
        this.hostsPerDay = hostsPerDay;
    }

    public void setIncomePerDay(String incomePerDay) {
        this.incomePerDay = incomePerDay;
    }

    public void setIncomePerMonth(String incomePerMonth) {
        this.incomePerMonth = incomePerMonth;
    }

    public void setIsHaveAdSense(String isHaveAdSense) {
        this.isHaveAdSense = isHaveAdSense;
    }

    public void setIsHaveYaDirect(String isHaveYaDirect) {
        this.isHaveYaDirect = isHaveYaDirect;
    }

    public void setDomainRegistered(String domainRegistered) {
        this.domainRegistered = domainRegistered;
    }

    public void setDomainExpires(String domainExpires) {
        this.domainExpires = domainExpires;
    }

    public void setAlexaRate(String alexaRate) {
        this.alexaRate = alexaRate;
    }

    public void setTyc(String tyc) {
        this.tyc = tyc;
    }

    public void setIsInDMOZ(String isInDMOZ) {
        this.isInDMOZ = isInDMOZ;
    }

    public void setIsInYACA(String isInYACA) {
        this.isInYACA = isInYACA;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getdomainName() {
        return domainName;
    }

    public String getId() {
        return id;
    }

    public String getLastCheck() {
        return lastCheck;
    }

    public String getLastCost() {
        return lastCost;
    }

    public String getViewsPerDay() {
        return viewsPerDay;
    }

    public String getHostsPerDay() {
        return hostsPerDay;
    }

    public String getIncomePerDay() {
        return incomePerDay;
    }

    public String getIncomePerMonth() {
        return incomePerMonth;
    }

    public String getIsHaveAdSense() {
        return isHaveAdSense;
    }

    public String getIsHaveYaDirect() {
        return isHaveYaDirect;
    }

    public String getDomainRegistered() {
        return domainRegistered;
    }

    public String getDomainExpires() {
        return domainExpires;
    }

    public String getAlexaRate() {
        return alexaRate;
    }

    public String getTyc() {
        return tyc;
    }

    public String getIsInDMOZ() {
        return isInDMOZ;
    }

    public String getIsInYACA() {
        return isInYACA;
    }

    public String getIp() {
        return ip;
    }
    
}
