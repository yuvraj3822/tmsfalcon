package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "multiple_other_vehicle_details")
data class MultipleOtherVehicleDetails   (
//        @PrimaryKey(autoGenerate = true) val id :Int? = 0,
        @PrimaryKey val id:Int? =0,
        @ColumnInfo(name = "unit_no") val unitNo:String,
        @ColumnInfo(name = "vehicle_year") val vehicleYear: String,
        @ColumnInfo(name = "vehicle_make") val vehicleMake: String,
        @ColumnInfo(name = "license_no") val licenseNos:String,
        @ColumnInfo(name = "license_state") val licenseState:String,


        @ColumnInfo(name = "owner_first_name") val owner_first_name:String,
        @ColumnInfo(name = "owner_phone_no") val owner_phone_no:String,
        @ColumnInfo(name = "owner_address") val owner_address:String,
        @ColumnInfo(name = "owner_city") val owner_city:String,
        @ColumnInfo(name = "owner_state") val owner_state:String,
        @ColumnInfo(name = "owner_zipcode") val owner_zipcode:String,



        @ColumnInfo(name = "is_insurance") val isInsurance:Boolean,
        @ColumnInfo(name = "insurance_company") val insuranceCompany: String,
        @ColumnInfo(name = "insurance_policy_no") val insurancePolicyNo:String,
        @ColumnInfo(name = "policy_holder_first_name") val policyHolderFirstName:String,
        @ColumnInfo(name = "policy_holder_last_name") val policyHolderLastName:String,
        @ColumnInfo(name = "driver_name") val ownerName:String,
        @ColumnInfo(name = "driver_phone_no") val ownerPhoneno:String,
        @ColumnInfo(name = "driver_address") val ownerAddress:String,
//        @ColumnInfo(name = "driver_dob") val ownerDob:String,
        @ColumnInfo(name = "driver_city") val ownerCity:String,
        @ColumnInfo(name = "driver_state") val ownerState:String,
        @ColumnInfo(name = "driver_zip_code") val ownerZipCode:String,
        @ColumnInfo(name = "is_vehicle_towed_away") val isVehicleTowedAway:Boolean,
        @ColumnInfo(name = "towed_company_name") val towedCompanyName:String,
        @ColumnInfo(name = "towed_company_phone_no") val towedCompanyPhnNo:String
)