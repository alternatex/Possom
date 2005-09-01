/*
 * Copyright (2005) Schibsted S�k AS
 * 
 */
package no.schibstedsok.front.searchportal.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import no.geodata.maputil.CoordHelper;
import no.geodata.maputil.MapPoint;
import no.geodata.maputil.MapEnvelope;


//webservices
import no.geodata.arcweb.AuthenticationLocator;
import no.geodata.arcweb.AuthenticationSoap;
import no.geodata.prod.webservices.arcweb.Envelope;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import no.geodata.prod.webservices.arcweb.MapImageInfo;
import no.geodata.prod.webservices.arcweb.MapImageLocator;
import no.geodata.prod.webservices.arcweb.MapImageOptions;
import no.geodata.prod.webservices.arcweb.MapImageSize;
import no.geodata.prod.webservices.arcweb.MapImageSoap;


/**
 *
 * @author hanst
 * @version
 *
 */
public class MapServlet extends HttpServlet {

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */

    //globale konstanter. Hvor b�r disse settes?? xml fil.
    final static long zoomLevel1 = 10000; //kartskala ved zoom til ett punkt
    final static long zoomLevel2 = 20000;
    final static long zoomLevel3 = 50000;
    final static long zoomLevel4 = 150000;
    final static long zoomLevel5 = 500000;
    final static double envFactor = 1.2; //faktor for � lage rom rundt envelope
    final static int imgWidth = 350;//bildest�rrelse i pixler, bredde
    final static int imgHeigth = 400;//bildest�rrelse i pixler, h�yde
    final static String datasource = "GEODATA.N50";
    final static String imgFormat = "png8";

    CoordHelper coordHelper = new CoordHelper();

    private String authenticate() throws RemoteException, ServiceException{
        //hvordan sjekke om vi allerede har en valid token? sende eksisterende token med i requesten? Kj�r auth.validateToken(token);
         AuthenticationLocator authLocator = new AuthenticationLocator();
         AuthenticationSoap auth = authLocator.getAuthenticationSoap();
         String token = auth.getToken("schi", "zofren");
         return token;
    }

    private String getUrl(String token, MapEnvelope me) throws RemoteException, ServiceException{
         String URL = new String();
         Envelope envelope = new Envelope();
         envelope.setMinX(me.getMinX());
         envelope.setMinY(me.getMinY());
         envelope.setMaxX(me.getMaxX());
         envelope.setMaxY(me.getMaxY());

         MapImageSize size = new MapImageSize();
         size.setWidth(imgWidth);
         size.setHeight(imgHeigth);

         MapImageOptions mapOptions = new MapImageOptions();
         mapOptions.setDataSource(datasource);
         mapOptions.setMapImageFormat(imgFormat);
         mapOptions.setMapImageSize(size);

         MapImageLocator mapimageLocator = new MapImageLocator();
         MapImageSoap mapImage = mapimageLocator.getMapImageSoap();
         MapImageInfo result = mapImage.getMap(envelope, mapOptions, token);
         URL = result.getMapURL();
         return URL;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        /* TODO output your page here
         *
         * - hent coordinate streng
         */
        String sUrl = new String();
        String token = new String();
        boolean retriveMapError = false;
        String sCoords = request.getParameter("coords");
        Vector vMapPoints = coordHelper.parseCoordString(sCoords);
        //Sjekk om det finnes noen koordinater, hvis ikke kan resten glemmes.
        if(!vMapPoints.isEmpty()){
            /*
             * - sjekke om requesten kommer fra s�ksiden eller navigering i kartet.
             */
            String action = request.getParameter("action");
            System.out.println("Innsendte action = "+action);
             /*
             * - if action == zoomin||zoomout||goW||panSW||panS||panSE||panE||panNE||panN||panNW {
              *     - hent gammelt env -> parameter == prevExtent (UTM koord)
              *     - filtrer vekk koordinater som er utenfor opprinnelig extent
              *     - hent gammel zoomlevel -> prevZoom.
              * }
              * else if (searchone||searchmany){
             * - hent zoomlevel. Mest aktuelt ved navigering, men kan ogs� v�re innsendt med ett s�ketreff.
             * - sjekk om det er et eller flere punkt
            */
            MapEnvelope me = new MapEnvelope();
            if(action.compareToIgnoreCase("viewone") == 0){//enkelt bedriftstreff. M� beregne envelope utifra ett pkt, zoomlevel og bildest�rrelse
                MapPoint mp = (MapPoint) vMapPoints.get(0);
                me = coordHelper.makeEnvelope(mp, zoomLevel1, imgWidth, imgHeigth);
            }
            else if(action.compareToIgnoreCase("viewmany") == 0){//enkelt bedriftstreff. M� beregne envelope utifra ett pkt, zoomlevel og bildest�rrelse
                me = coordHelper.makeEnvelope(vMapPoints, imgWidth, imgHeigth, envFactor);
            }

            try {
                token = authenticate();
                sUrl = getUrl(token, me);
            }
            catch (ServiceException serviceExcep) {
            }
            catch (RemoteException remoteExcep){

            }

            /*
             * - lag envelope
              *     - utifra ett punkt
              *     - utifra flere pkt.
             * - lag pixelarray
             * - kall webservice
              */
        }//if(!vMapPoints.isEmpty()){

        if (!retriveMapError){
            response.sendRedirect(sUrl);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);

    }

    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }

    /*
     * Parser koordinatstreng til ett arra
     */
    protected void makeCoordArray(String coords){
        //Array
    }
    // </editor-fold>
}


