package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "basic_form_data")
data class BasicFormAccident(
        @PrimaryKey val id :Int,
        @ColumnInfo(name = "incident_type") val incidentType:String,
        @ColumnInfo(name = "date") val date: String,
        @ColumnInfo(name = "time") val time: String,
        @ColumnInfo(name = "description") val description: String,
        @ColumnInfo(name = "type") val type:String,
        @ColumnInfo(name = "private_property") val isPrivateProperty:Boolean,
//        @ColumnInfo(name = "is_driving_for_employer") val isDrivingForEmployer:Boolean,
        @ColumnInfo(name = "employer_name") val driverName: String,
        @ColumnInfo(name = "employer_phoneNo") val driverPhoneNo:String,

        @ColumnInfo(name = "driver_address") val driverAddress:String,
        @ColumnInfo(name = "driver_city") val driverCity:String,
        @ColumnInfo(name = "driver_state") val driverState:String,
        @ColumnInfo(name = "driver_zipcode") val driverZipCode:String,

//        @ColumnInfo(name = "employer_insurance_provider") val employerInsuranceProvider:String,
//        @ColumnInfo(name = "employer_insurance_policy_no") val employerInsurancePolicyNo:String,
        @ColumnInfo(name = "lat") val lat:String,
        @ColumnInfo(name = "lng") val lng:String,
        @ColumnInfo(name = "license_no") val licenseNo:String,
        @ColumnInfo(name = "dob") val dob:String,
        @ColumnInfo(name = "telephone_no") val telephoneNo:String,
        @ColumnInfo(name = "vehicle_year_make") val vehicleYearMake:String,
        @ColumnInfo(name = "license_plate_vehicle_identification_no") val licensePlateVehicleIdNo:String

)