package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "multiple_vehicle_details")
data class MultipleVehicleDetails   (

        @PrimaryKey val id:Int? =0,


        //
//
//
//
//

        @ColumnInfo(name = "truck_unit_no_dialog") val truck_unit_no_dialog:String,
        @ColumnInfo(name = "truck_year") val truck_year:String,
        @ColumnInfo(name = "truck_make") val truck_make:String,
        @ColumnInfo(name = "truck_license_no") val truck_license_no:String,
        @ColumnInfo(name = "truck_license_state") val truck_license_state:String,



        @ColumnInfo(name = "trailer_no") val trailerNo:String,
        @ColumnInfo(name = "vehicle_year") val vehicleYear: String,
        @ColumnInfo(name = "vehicle_make") val vehicleMake: String,
//        @ColumnInfo(name = "dot_no") val dotNo:String,
        @ColumnInfo(name = "license_no") val licenseNos:String,
        @ColumnInfo(name = "license_state") val licenseState:String,


        @ColumnInfo(name = "insurance_claim_by_driver_or_truck") val insuranceClaimByDriverOrTruck: String,
        @ColumnInfo(name = "insurance_company") val insuranceCompany: String,
        @ColumnInfo(name = "insurance_policy_no") val insurancePolicyNo:String,
        @ColumnInfo(name = "policy_holder_phoneNo") val policyHolderPhoneNo:String,
        @ColumnInfo(name = "policy_holder_email") val policyHolderEmail:String,


        @ColumnInfo(name = "trailer_insurance_company") val trailerInsuranceCompany: String,
        @ColumnInfo(name = "trailer_insurance_policy_no") val trailerInsurancePolicyNo:String,
        @ColumnInfo(name = "trailer_policy_holder_phoneNo") val trailerPolicyHolderPhoneNo:String,
        @ColumnInfo(name = "trailer_policy_holder_email") val trailerPolicyHolderEmail:String,

        @ColumnInfo(name = "owner_name") val ownerName:String,
        @ColumnInfo(name = "owner_phone_no") val ownerPhnNo:String,
        @ColumnInfo(name = "owner_address") val ownerAddress:String,
        @ColumnInfo(name = "owner_dob") val ownerDob:String,
        @ColumnInfo(name = "owner_city") val ownerCity:String,
        @ColumnInfo(name = "owner_state") val ownerState:String,
        @ColumnInfo(name = "owner_zip_code") val ownerZipCode:String,
        @ColumnInfo(name = "is_vehicle_towed_away") val isVehicleTowedAway:Boolean,
        @ColumnInfo(name = "towed_company_name") val towedCompanyName:String,
        @ColumnInfo(name = "towed_company_phone_no") val towedCompanyPhnNo:String

)