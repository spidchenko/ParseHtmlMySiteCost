/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsehtmlmysitecost;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlparser.*;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.NodeList;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 *
 * @author spidchenko.d
 */
public class ParseHtmlMySiteCost {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        getSiteStats();
    }
    
    /**
     * Получим результаты анализа сайтов из БД, которые были спарсены методом getSites()
     */
    public static void getSiteStats() {
        
        DBConnection dbConnection = new DBConnection();
//        int numSitesToParse = dbConnection.getNumSitesToParse();
//        Site currentSite = new Site();
        int numSitesToParse;
        int numSitesAlreadyParsed = 0;
        long  timeStartMills = DateTimeUtils.currentTimeMillis();
        long timePassMills;
        //LocalTime timePass = new LocalTime();
        //LocalTime timeNow = new LocalTime();
        //timeStart = timePass = LocalTime.now();
        do{
            numSitesToParse = dbConnection.getNumSitesToParse();
            Site currentSite = new Site();
        
            try {
                
                dbConnection.initNextSiteToParse(currentSite);
                System.out.println(currentSite.getdomainName());
                
                Parser parser = new Parser("http://mysitecost.ru/check/"+currentSite.getdomainName());//("http://mysitecost.ru/check/"+currentSiteToParse);
                parser.setEncoding("UTF-8");
                NodeList nodeList = parser.parse(new TagNameFilter("title"));
                
                if(nodeList.elementAt(0).getChildren().toHtml().contains("Внимание")){
                    throw new ParserException("Exception getting input stream");    //Бросим исключение если эта страница не содержит данных сайта
                }
                
                parser.reset();
                //Получим все div, отфильтруем требуемый с датой последней проверки
                nodeList = parser.parse(new HasAttributeFilter("id","last_update"));
                //Вытащим из div'a текст с датой последней оценки (элемент номер 1, наследник - содержимое div'a)
                String lastCheck = nodeList.elementAt(0).getChildren().toHtml();
                //System.out.println(lastCheck);
                if(lastCheck.lastIndexOf("Последний раз сайт оценивался ") == 0){
                    lastCheck = lastCheck.substring(30);    //Отрезаем от текста "Последний раз сайт оценивался " если эта подстрока найдена
                } else{
                    System.err.println("Ошибка парсинга тега даты! Содержимое тега - "+lastCheck);
                }
                
                if(lastCheck.lastIndexOf("вчера") == 0){    //Если там не дата, а слово "вчера"
                    lastCheck = DateTime.now().minusDays(1).toString("dd.MM.yyyy")+lastCheck.substring(5);
                }
                
                 if(lastCheck.lastIndexOf("сегодня") == 0){ //Если там не дата, а слово "сегодня"
                    lastCheck = DateTime.now().toString("dd.MM.yyyy")+lastCheck.substring(7);
                }               
                
                //        System.out.println(lastCheck+" \tlastCheck");
                //Переформатируем дату-время в MySQL формат (05.08.2015 в 11:12 -> 2015-08-05 11:12:00) используя фозможности пакета joda-time
                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy в HH:mm");
                DateTime dateTimeToFormat = formatter.parseDateTime(lastCheck);

                lastCheck = dateTimeToFormat.toString("yyyy-MM-dd HH:mm:ss");
                currentSite.setLastCheck(lastCheck);
                //        System.out.println(currentSite.getLastCheck()+" \tLastCheck");
                //------------------
                parser.reset();
                nodeList = parser.parse(new HasAttributeFilter("class", "myh1"));
                //Вытащим из span'a текст со стоимостью (элемент номер 1, наследник - содержимое span'a)
                currentSite.setLastCost(nodeList.elementAt(0).getChildren().toHtml().replaceAll("[$,]", ""));
                //        System.out.println(currentSite.getLastCost()+" \t\tLastCost");
                //------------------
                parser.reset();
                nodeList = parser.parse(new HasAttributeFilter("class", "b9"));
                //Вытащим из td текст с количеством просмотров в сутки (элемент номер 1, наследник - содержимое td, число между тегами <b>)
                currentSite.setViewsPerDay(nodeList.elementAt(0).getChildren().elementAt(1).toHtml().replaceAll("[,]", ""));
                //        System.out.println(currentSite.getViewsPerDay()+" \t\tViewsPerDay");
                //------------------

                //Вытащим из td текст с количеством хостов в сутки (элемент номер 2, наследник - содержимое td, число между тегами <b>)
                currentSite.setHostsPerDay(nodeList.elementAt(1).getChildren().elementAt(1).toHtml().replaceAll("[,]", ""));
                //        System.out.println(currentSite.getHostsPerDay()+" \t\tHostsPerDay");
                //------------------
                currentSite.setAlexaRate(nodeList.elementAt(3).getChildren().elementAt(1).toHtml());
                //        System.out.println(currentSite.getAlexaRate()+" \t\tAlexaRate");
                //------------------
                currentSite.setTyc(nodeList.elementAt(4).getChildren().elementAt(1).toHtml());
                //        System.out.println(currentSite.getTyc()+" \t\tgetTyc");
                //------------------
                parser.reset();
                nodeList = parser.parse(new HasAttributeFilter("class", "b7"));
                //Вытащим из td текст с доходом в сутки (элемент номер 1, наследник - содержимое td, число между тегами <b>)
                currentSite.setIncomePerDay(nodeList.elementAt(0).getChildren().elementAt(1).toHtml().replaceAll("[$,]", ""));
                //        System.out.println(currentSite.getIncomePerDay()+" \t\tIncomePerDay");
                //------------------
                //Вытащим из td текст с доходом в месяц (элемент номер 1, наследник - содержимое td, число между тегами <b>)
                currentSite.setIncomePerMonth(nodeList.elementAt(1).getChildren().elementAt(1).toHtml().replaceAll("[$,]", ""));
                //        System.out.println(currentSite.getIncomePerMonth()+" \t\tIncomePerMonth");
                //------------------
                //Вытащим из td текст с проверкой рекламной сети AdSense (элемент номер 1, наследник - содержимое td, текст после тегов <b></b> "Да - Нет")
                currentSite.setIsHaveAdSense(nodeList.elementAt(2).getChildren().elementAt(3).toHtml().trim());
                //        System.out.println(currentSite.getIsHaveAdSense()+" \t\tIsHaveAdSense");
                //------------------
                //Вытащим из td текст с проверкой рекламной сети YandexDirect (элемент номер 1, наследник - содержимое td, текст после тегов <b></b> "Да - Нет")
                currentSite.setIsHaveYaDirect(nodeList.elementAt(3).getChildren().elementAt(3).toHtml().trim());
                //        System.out.println(currentSite.getIsHaveYaDirect()+" \t\tIsHaveYaDirect");
                //------------------
                currentSite.setDomainRegistered(nodeList.elementAt(4).getChildren().toHtml());
                //        System.out.println(currentSite.getDomainRegistered()+" \tDomainRegistered");
                if (currentSite.getDomainRegistered().contains("<b>0</b>")) throw new ParserException("Exception getting input stream");
                //------------------
                currentSite.setDomainExpires(nodeList.elementAt(5).getChildren().toHtml());
                //        System.out.println(currentSite.getDomainExpires()+" \tDomainExpires");
                //------------------
                currentSite.setIsInDMOZ(nodeList.elementAt(10).getChildren().elementAt(3).toHtml().trim());
                //        System.out.println(currentSite.getIsInDMOZ()+" \t\tIsInDMOZ");
                //------------------
                currentSite.setIsInYACA(nodeList.elementAt(12).getChildren().elementAt(3).toHtml().trim());
                //        System.out.println(currentSite.getIsInYACA()+" \t\tIsInYACA");
                //------------------
                parser.reset();
                nodeList = parser.parse(new HasAttributeFilter("style", "font-weight:bold;text-decoration:underline"));
                currentSite.setIp(nodeList.elementAt(0).getChildren().toHtml());
                //        System.out.println(currentSite.getIp());
                dbConnection.writeSiteToDatabase(currentSite);
            } catch (ParserException ex) {
                if(ex.getMessage().contains("Exception getting input stream")){
                    //Если нет такой страницы, то сделаем пустую запись в базу
                    System.err.println("404! "+currentSite.getdomainName());
                    currentSite = new Site();
                    dbConnection.initNextSiteToParse(currentSite);
                    dbConnection.writeSiteToDatabase(currentSite);
                } else{
                    Logger.getLogger(ParseHtmlMySiteCost.class.getName()).log(Level.SEVERE, null, ex);
                }       
            }
            numSitesAlreadyParsed++;
            //DateTimeUtils.setCurrentMillisOffset(-timeStartMills);
            
            
          
            if((numSitesAlreadyParsed % 10) == 0){
                timePassMills = DateTimeUtils.currentTimeMillis() - timeStartMills;
                Period period = new Period(timePassMills*numSitesToParse/numSitesAlreadyParsed);
                System.out.println(period.getHours()+" hours "+period.getMinutes()+" minutes to the end. "+numSitesToParse+" sites left");
            }
        
        }while(numSitesToParse > 0);
    }
    
    
    
    /**
     * Получим список сайтов со страниц рейтинга и сохраним их в БД
     */
    public static void getSites() throws ParserException, IOException {
        
        DBConnection dbConnection = new DBConnection();
   
        int pageNum = 2579; //Первая страница для парсинга
        
        searchInPages:
        do{

            try{
                Parser parser = new Parser("http://mysitecost.ru/country/Russian+Federation/page-"+pageNum+".html");
                parser.setEncoding("UTF-8");
                System.out.println(parser.getURL());
                //Получим все td с потомками:
                NodeList b1NodeList = parser.parse(new HasAttributeFilter("class", "b1")); 
                //parser.reset();
                //NodeList b2NodeList = parser.parse(new HasAttributeFilter("class", "b2"));
                parser.reset();
                NodeList b3NodeList = parser.parse(new HasAttributeFilter("class", "b3")); 
               // b3NodeList.keepAllNodesThatMatch(new TagNameFilter("A"));
                //Отфильтруем только нужные:
                //divNodeList.keepAllNodesThatMatch(new HasAttributeFilter("class", "b1"));
                System.out.println("Page: "+pageNum);
                //System.out.println("Num b1 - "+b1NodeList.size()+". Num b3 - "+b3NodeList.size());
        
                //Рекурсивный фильтр тегов ссылок:
                b3NodeList = b3NodeList.extractAllNodesThatMatch(new TagNameFilter("A"), true);
                // System.out.println(b3NodeList.toHtml());
               
                //Рекурсивный фильтр тегов стоимости
                b1NodeList = b1NodeList.extractAllNodesThatMatch(new HasAttributeFilter("valign","top"), true);
                //System.out.println(b1NodeList.toHtml());
               
               
                String currentDomain;
                String currentCost;
                for(int i = 0; i < b3NodeList.size(); i++){
                    //Вывод содержимого ссылок:
                    currentDomain = b3NodeList.elementAt(i).getChildren().toHtml().toLowerCase();
                    //Вывод содержимого тегов стоимости:
                    currentCost = b1NodeList.elementAt(i).getChildren().toHtml().replaceAll("[$,]", "");
                    //Запись в БД по одному домену (нужна обработка ошибок группового добавления)
                    dbConnection.setNewDomain(currentDomain,currentCost);
//                    temp = temp.extractAllNodesThatMatch(new TagNameFilter("A"), true);
//                    System.out.println(temp.toHtml());
                }

                pageNum++;

            } catch(org.htmlparser.util.ParserException ex){
                System.err.println("Parser exception, trying again...");
            }
                        
        }while (pageNum < 12665);   //Последняя страница
 
    }
}