package com.example.suhei.irent;

import java.util.Date;
import com.google.firebase.firestore.ServerTimestamp;

public class postProperty {

	public postProperty() {

	}

	public String owner_id, property_name, property_id, house_type, no_of_bedrooms, no_of_bathrooms, occupants, image_url, image_thumb, tenure_period, prior_notification, additional_rules, security_deposit, advance_rental, key_deposit, monthly_rental;
	public Date date_posted;

	public String getOwner_id() {
		return owner_id;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}

	public String getProperty_name() {
		return property_name;
	}

	public void setProperty_name(String property_name) {
		this.property_name = property_name;
	}

	public String getProperty_id() {
		return property_id;
	}

	public void setProperty_id(String property_id) {
		this.property_id = property_id;
	}

	public String getHouse_type() {
		return house_type;
	}

	public void setHouse_type(String house_type) {
		this.house_type = house_type;
	}

	public String getNo_of_bedrooms() {
		return no_of_bedrooms;
	}

	public void setNo_of_bedrooms(String no_of_bedrooms) {
		this.no_of_bedrooms = no_of_bedrooms;
	}

	public String getNo_of_bathrooms() {
		return no_of_bathrooms;
	}

	public void setNo_of_bathrooms(String no_of_bathrooms) {
		this.no_of_bathrooms = no_of_bathrooms;
	}

	public String getOccupants() {
		return occupants;
	}

	public void setOccupants(String occupants) {
		this.occupants = occupants;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getImage_thumb() {
		return image_thumb;
	}

	public void setImage_thumb(String image_thumb) {
		this.image_thumb = image_thumb;
	}

	public String getTenure_period() {
		return tenure_period;
	}

	public void setTenure_period(String tenure_period) {
		this.tenure_period = tenure_period;
	}

	public String getPrior_notification() {
		return prior_notification;
	}

	public void setPrior_notification(String prior_notification) {
		this.prior_notification = prior_notification;
	}

	public String getAdditional_rules() {
		return additional_rules;
	}

	public void setAdditional_rules(String additional_rules) {
		this.additional_rules = additional_rules;
	}

	public String getSecurity_deposit() {
		return security_deposit;
	}

	public void setSecurity_deposit(String security_deposit) {
		this.security_deposit = security_deposit;
	}

	public String getAdvance_rental() {
		return advance_rental;
	}

	public void setAdvance_rental(String advance_rental) {
		this.advance_rental = advance_rental;
	}

	public String getKey_deposit() {
		return key_deposit;
	}

	public void setKey_deposit(String key_deposit) {
		this.key_deposit = key_deposit;
	}

	public String getMonthly_rental() {
		return monthly_rental;
	}

	public void setMonthly_rental(String monthly_rental) {
		this.monthly_rental = monthly_rental;
	}

	public Date getDate_posted() {
		return date_posted;
	}

	public void setDate_posted(Date date_posted) {
		this.date_posted = date_posted;
	}

	public postProperty(String owner_id, String property_name, String property_id, String house_type, String no_of_bedrooms, String no_of_bathrooms, String occupants, String image_url, String image_thumb, String tenure_period, String prior_notification, String additional_rules, String security_deposit, String advance_rental, String key_deposit, String monthly_rental, Date date_posted) {
		this.owner_id = owner_id;
		this.property_name = property_name;
		this.property_id = property_id;
		this.house_type = house_type;
		this.no_of_bedrooms = no_of_bedrooms;
		this.no_of_bathrooms = no_of_bathrooms;
		this.occupants = occupants;
		this.image_url = image_url;
		this.image_thumb = image_thumb;
		this.tenure_period = tenure_period;
		this.prior_notification = prior_notification;
		this.additional_rules = additional_rules;
		this.security_deposit = security_deposit;
		this.advance_rental = advance_rental;
		this.key_deposit = key_deposit;
		this.monthly_rental = monthly_rental;
		this.date_posted = date_posted;
	}
}

