/* Copyright (2006-2007) Schibsted Søk AS
 * This file is part of SESAT.
 * You can use, redistribute, and/or modify it, under the terms of the SESAT License.
 * You should have received a copy of the SESAT License along with this program.  
 * If not, see https://dev.sesat.no/confluence/display/SESAT/SESAT+License

 */
package no.sesat.search.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A simple implementation of a search result item that may contain products.
 * 
 * @author <a href="mailto:lars.johansson@conduct.no">Lars Johansson</a>
 * @version <tt>$Id$</tt>
 */
public final class CatalogueSearchResultItem extends BasicResultItem {
	
    private static final int PRODUCT_STORKUNDE_M = 152;
    private static final int PRODUCT_STORKUNDE_L = 151;
    private static final int PRODUCT_STORKUNDE_XL = 150;
    private static final int PRODUCT_STORKUNDE_EKSKL = 149;
    private static final int PRODUCT_PAKKE_L = 123;
    private static final int PRODUCT_PAKKE_M = 122;
    private static final int PRODUCT_PAKKE_S = 121;
    private static final int PRODUCT_INFOSIDE = 120;
    private static final int PRODUCT_UTVIDET_OPPFORING = 119;
    private static final int PRODUCT_PAKKE_EKSKLUSIV = 116;
    private static final int PRODUCT_PAKKE_XL = 3;

    private static final Logger LOG = Logger.getLogger(CatalogueSearchResultItem.class);
    private static final String SEPARATOR = ", ";

    private CompanyAccounting companyAccounting = null;
    
    /**
	 * TODO: javadoc.
	 */
    private ProductResult products = null;

    /**
	 * TODO javadoc.
	 * 
	 * @param products
	 */
    public void addProducts(final ProductResult products) {
    	this.products = products;
    }
    
    /**
	 * TODO javadoc.
	 * 
	 * @return
	 */
    public boolean hasProducts(){
    	return this.products != null ? true : false;
    }
    
    /**
	 * TODO javadoc.
	 * 
	 * @return
	 */
    public ProductResult getProducts(){
    	return this.products;
    }
    
    /**
	 * Responsible for building html meta description content. <meta
	 * name='description' content="data from method." />
	 * 
	 * @return Comma separated values for content attribute.
	 */
    public String getHtmlMetaDescriptionContent(){
    	StringBuffer metaDataDescrption = new StringBuffer();
    	
    	if(getMarketName() != null){
    		metaDataDescrption.append(
    				getMarketName().substring(0,1).toUpperCase() + 
    				getMarketName().substring(1, getMarketName().length()));
    		
    		metaDataDescrption.append(SEPARATOR);
    	}
    	
    	if(getDescription() != null && getDescription().trim().length()>0){
    		metaDataDescrption.append(getDescription());
    		metaDataDescrption.append(SEPARATOR);
    	}
    	
    	if(getPhoneNumber()!=null && getPhoneNumber().trim().length()>0){
	    	metaDataDescrption.append("Tlf: "+getPhoneNumber());
	    	metaDataDescrption.append(SEPARATOR);
    	}
    	
    	if(getFax() != null && getFax().trim().length()>0){
    		metaDataDescrption.append("Fax: " + getFax());
    		metaDataDescrption.append(SEPARATOR);
    	}
    	
    	if(getHomePageURL()!=null && getHomePageURL().trim().length()>0){
	    	metaDataDescrption.append(getHomePageURL());
	    	metaDataDescrption.append(SEPARATOR);
    	}
    	
    	if(getAddress()!=null && getAddress().trim().length()>0){
	    	metaDataDescrption.append("Adresse: " + getAddress());
	    	metaDataDescrption.append(SEPARATOR);
    	}
    	
    	if(getField("iypkommune")!=null && getField("iypkommune").trim().length()>0){    	
    		metaDataDescrption.append(getField("iypkommune"));
    	}
    	
    	return metaDataDescrption.toString();
    }
    
    /**
	 * Responsible for building the value of the keyword metadata content
	 * attribute. <meta name='keywords' content='result from this method' />
	 * 
	 * @return Comma separated values for keywords.
	 */
    public String getHtmlMetaKeywordsContent(){
    	StringBuffer metaDataKeywords 
    		= new StringBuffer(getHtmlMetaDescriptionContent());
    	
    	if(getKeywords() != null){
    		metaDataKeywords.append(SEPARATOR);
    		metaDataKeywords.append(getKeywords());
    	}
    	
    	return metaDataKeywords.toString();
    }
    
    /**
	 * Util method for getting keywords from search result.
	 * 
	 * @return Comma separated keywords.
	 */
    private String getKeywords() {
        if (products != null) {
            final List<ProductResultItem> infoPageProducts = products.getInfoPageProducts();
            if (infoPageProducts != null && infoPageProducts.size() > 0) {
                return infoPageProducts.get(0).getField("keywords");
            }
        }
    	
        return null;
    }
    
    /**
	 * Return all keywords.
	 * 
	 * @return
	 */
    public Collection<String> getKeywordsCollection() {
        List<String> keywordsList = null;
        final String keywords = getKeywords();
        if (keywords != null && !keywords.trim().equals("")) {
            final String[] keywordsArray = keywords.split(",");
            keywordsList = Arrays.asList(keywordsArray);
        }
        return keywordsList;
    }

    
    private String getDescription(){
    	if(products != null && products.getInfoPageProducts() != null && products.getInfoPageProducts().size() > 0){
    		return products.getInfoPageProducts().get(0).getField("textShort");
    	}
    	return null;
    }
    
    public String getMarketName(){
    	if(products != null && products.getInfoPageProducts() != null && products.getInfoPageProducts().size() > 0){
    		return products.getInfoPageProducts().get(0).getField("marketname");
    	}
    	return null;
    }
    
    private String getFax(){
    	if(products != null && products.getInfoPageProducts() != null && products.getInfoPageProducts().size() > 0){
    		return products.getInfoPageProducts().get(0).getField("fax");
    	}
    	
    	return null;
    }

    
    /**
	 * Utility method for checking if the entry belongs to a paying customer.
	 * 
	 * @return true if the result item is an entry for a paying customer.
	 */
    public boolean isCommercial(){
    	String productPackage = getField("iyppakke");
    	if(productPackage != null && productPackage.length() > 0){
    		return true;
    	}
    	return false;
    }
    
    /**
	 * Responsible for getting the phone number to display for this result item.
	 * 
	 * @return The phone number to display for this result item.
	 */
    public String getPhoneNumber(){
    	String phoneNumber = getField("iypnrtelefon");
    	if(phoneNumber != null && phoneNumber.length() > 0){
    		return phoneNumber;
    	}
    	return null;
    }
    
    /**
	 * Returns a description of the result item.
	 * 
	 * @return a description of the result item.
	 */
    public String getCompanyDescription(){
    	String description = getField("iyplogotekst");
        
    	if(isCommercial() && (description != null && description.length() > 0)){
    		return description;
    	}

        return null;
    }
    
    /**
	 * Returns the url for the logo to display for this result item.
	 * 
	 * @return URL for the logo to display.
	 */
    public String getLogoURL(){
    	// TODO: check product
    	if(!isCommercial()){
    		return null;
    	}
    	
    	final String logoURL = getField("iyplogourl");
    	if(logoURL != null && logoURL.length() > 0){
    		return logoURL;
    	}
    	return null;
    }
    
    
    /**
	 * Returns the address for for this result item.
	 * 
	 * @return The address in format: street, zipcode city.
	 */
    public String getAddress(){
       
    	String address = getField("iypadresse");
    	String zipCode = getField("iyppostnr");
    	String city = getField("iyppoststed");
    	
    	StringBuffer compositAddress = new StringBuffer();
    	if(address != null && address.length() > 0){
    		compositAddress.append(address);
    		compositAddress.append(", ");
    	}
    	
    	if(zipCode != null && zipCode.length() > 0){
    		compositAddress.append(zipCode);
    	}
    	
    	if(city != null && city.length() > 0){
    		compositAddress.append(" ");
    		compositAddress.append(city);
    	}
    	
    	if(compositAddress.length() > 0){
    		return compositAddress.toString();
    	}
    	return null;
    }
    
    public String getEmailAddress(){
    	// TODO: check product.
    	if(!isCommercial()){
    		return null;
    	}
    	
    	String email = getField("iypepost");
    	if(email != null && email.length() > 0){
    		return email;
    	}
    	return null;
    }
    
    /**
	 * Returns the url of the homepage for this result item.
	 * 
	 * @return The URL of the homepage for a paying customer.
	 */
    public String getHomePageURL(){
    	if(!isCommercial()){
    		return null;
    	}
    	// TODO: check product.
    	String homePageURL = getField("iypurl");
    	if(homePageURL != null && homePageURL.length() > 0){
    		return "http://"+homePageURL;
    	}
    	return null;
    }
    
    public String imagePart(final String stringToSplit){
     
        if(stringToSplit == null || stringToSplit.length() < 1){
            return null;
        }
        
        String[] imageAndUrl = stringToSplit.split(";");
        return imageAndUrl[0];
    }
    
    public String urlPart(final String stringToSplit){
        if(stringToSplit == null || stringToSplit.length() < 1 || !stringToSplit.contains(";")
                || stringToSplit.lastIndexOf(";") == (stringToSplit.length()-1)) {
            return null;
        }
        
        String[] imageAndUrl = stringToSplit.split(";");
        return imageAndUrl[1];
    }

    
    
    /**
	 * Checks if the result item should be rendered with a bold title.
	 * 
	 * @return true if the product has bold title.
	 */
    public boolean isTitleBold(){
    	String productPackage = getField("iyppakke");
    	if(productPackage != null && productPackage.length() > 0){
    		try{
    			// TODO: move bold indicator to index to avoid hardcoding of
				// product and rules.
    			int packageId = Integer.parseInt(productPackage);
    			if (packageId == PRODUCT_PAKKE_XL || packageId == PRODUCT_PAKKE_EKSKLUSIV
                        || packageId == PRODUCT_UTVIDET_OPPFORING || packageId == PRODUCT_INFOSIDE
                        || packageId == PRODUCT_PAKKE_S || packageId == PRODUCT_PAKKE_M || packageId == PRODUCT_PAKKE_L
                        || packageId == PRODUCT_STORKUNDE_EKSKL || packageId == PRODUCT_STORKUNDE_XL
                        || packageId == PRODUCT_STORKUNDE_L || packageId == PRODUCT_STORKUNDE_M) {
                    return true;
                    
                    
                }else if(packageId>=1002){
                	/**
					 * If packageId is greater than 1002, then this packageId is
					 * not a productId but a priority rating from the new
					 * priority system that will replace the old system with
					 * hard coded values of productId.
					 */
                	return true;
                }
    		}catch(NumberFormatException e){
    			return false;
    		}
    	}
    	return false;
    }
    
    
	public void setCompanyAccounting(CompanyAccounting accounting){
		companyAccounting = accounting;
	}
	
	public CompanyAccounting getCompanyAccounting(){
		return companyAccounting;
	}
	
	public boolean hasCompanyAccounting(){
		return companyAccounting!=null;
	}    
}
