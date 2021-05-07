package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import butterknife.ButterKnife
import butterknife.OnClick
import com.androidnetworking.common.ANRequest
import com.androidnetworking.common.ANRequest.MultiPartBuilder
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.gson.JsonArray
import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.blob.CloudBlobContainer
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.customtools.*
import com.tmsfalcon.device.tmsfalcon.customtools.AppController.AccidentContainerName
import com.tmsfalcon.device.tmsfalcon.database.AccidentBasicDetails
import com.tmsfalcon.device.tmsfalcon.database.AccidentCaptureImageTable
import com.tmsfalcon.device.tmsfalcon.database.AccidentWitnessTable
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.*
import com.tmsfalcon.device.tmsfalcon.entities.AccidentCaptureModel
import com.tmsfalcon.device.tmsfalcon.entities.AccidentRecordsRequest
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel
import kotlinx.android.synthetic.main.upload_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileWriter
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class KotAccidentFinalScreen : NavigationBaseActivity() {

    var networkValidator: NetworkValidator? = null
    var customValidator: CustomValidator? = null
    var session: SessionManager? = null
    var db: AccidentBasicDetails? = null
    var witnessTable: AccidentWitnessTable? = null
    var accidentRecordsRequest: AccidentRecordsRequest? = null
    var document_db: AccidentCaptureImageTable? = null
    lateinit var document_hashmap: MutableMap<String, File>
    lateinit var db1: AccidentModuleDb
    var imageHelper: ImageHelper? = null
    var typeBasic = "basic"
    var typeOther = "other"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val contentView = inflater.inflate(R.layout.activity_accident_final_screen, null, false)
        val contentView = inflater.inflate(R.layout.upload_layout, null, false)
        drawer.addView(contentView, 0)
        ButterKnife.bind(this)
        initIds()
        main_layout!!.init(this@KotAccidentFinalScreen)

        /*
        if(networkValidator.isNetworkConnected()){
            upload_later_btn.setVisibility(View.GONE);
            upload_later_text.setVisibility(View.GONE);
        }
        else{
            upload_now_btn.setVisibility(View.VISIBLE);
        }
        */

    }

    private fun initIds() {

        networkValidator = NetworkValidator(this@KotAccidentFinalScreen)
        customValidator = CustomValidator(this@KotAccidentFinalScreen)
        session = SessionManager(this@KotAccidentFinalScreen)
        db = AccidentBasicDetails(this@KotAccidentFinalScreen)
        document_db = AccidentCaptureImageTable(this@KotAccidentFinalScreen)
        witnessTable = AccidentWitnessTable(this@KotAccidentFinalScreen)
        accidentRecordsRequest = AccidentRecordsRequest()
        db1 = AccidentModuleDb.getDatabase(this@KotAccidentFinalScreen)
        imageHelper = ImageHelper()


    }






    fun uploadImagesFor(){
        CoroutineScope(IO).launch {
          var a =   db1.wfaDao().getWitnessFormData()
           var list = ArrayList<String>()
            for (well in a){
                list.add(well.witnessAudioUrl)
            }
            uploadImageAndAudioFile(prepareListToUploadFiles(list))
        }
    }







    fun makeNewJson(){

        var mainObj = JSONObject()
        var basic_form_data = JSONArray()
        var multiple_vehicle_details = JSONArray()
        var damage_detail = JSONArray()
        var witness_detail = JSONArray()
        var capture_screen = JSONArray()
        var sign_report = JSONObject()

//        create json for basic form data
        var jsonBasic = basicFormData()
        var jsonOther = otherFormData()
        basic_form_data.put(jsonBasic)
        basic_form_data.put(jsonOther)

//        create json for multiple vehicles
        multiple_vehicle_details = prepareMultipleJSon()

//        create json for Damges
        damage_detail = prepareDamageDetail()

//        create json for Witness
        witness_detail = prepareWitnessDetail()

//        create json for Capture
        capture_screen = prepareForCapture()

//        create json for Sign
        sign_report = prepareSignReport()

        var listImage = db1.csaDao().getImageListss()
        var listWitnessFormData = db1.wfaDao().getWitnessFormData()

        var listImageAudio:ArrayList<String> = ArrayList()
        for (item in listImage){
            listImageAudio.add(item.image_url)
        }
        for (item in listWitnessFormData){
            listImageAudio.add(item.witnessAudioUrl)
        }

        /**
         * create json variable
         */

        mainObj.put("basic_form_data",basic_form_data)
        mainObj.put("multiple_vehicle_details",multiple_vehicle_details)
        mainObj.put("damage_detail",damage_detail)
        mainObj.put("witness_detail",witness_detail)
        mainObj.put("capture_screen",capture_screen)
        mainObj.put("sign_report",sign_report)
        mainObj.put("azure_event_trigger",1)
        mainObj.put("token", session?._token)


        Log.e("mainObj"," :"+mainObj.toString())

//        val container = getJsonContainer("traveloko-documents")



        val container = getJsonContainer(AccidentContainerName)


        var date = System.currentTimeMillis()

        var jsonDocName :String = "signed-accident-doc-"+date.toString()

        val imageBlob = container?.getBlockBlobReference(jsonDocName + ".json")

        // Constructs a FileWriter given a file name, using the platform's default charset
        var  file = File(ImageHelper.root + "/" + jsonDocName + ".json")
        val fileWriter = arrayOf<FileWriter?>(null)

        fileWriter[0] = FileWriter(file)
        fileWriter[0]!!.write(mainObj.toString())

        if (fileWriter[0] != null) {
            Log.e("insideFileWriter", ": ")
            fileWriter[0]!!.flush()
            fileWriter[0]!!.close()
        }

        imageBlob?.uploadFromFile(file.absolutePath)
        Log.e("uploaded","successfully")

        session?.uploadTheSavedData()

        CoroutineScope(Main).launch {
            progress_bar.visibility = View.GONE
        }

        navigateToRespectiveScreen()





    }




    fun otherFormData():JSONObject{
        var otherAccidentForm = db1.ofaDao().getBasicFormData()

        /**
         * create json object
         * for other form data
         */

        var jsonOtherForm = JSONObject()
        jsonOtherForm.put("type_screen", typeOther)

        jsonOtherForm.put("incident_type", "")
        jsonOtherForm.put("date", otherAccidentForm.date)
        jsonOtherForm.put("time", otherAccidentForm.time)
        jsonOtherForm.put("type", otherAccidentForm.type)
        jsonOtherForm.put("description", "")

        jsonOtherForm.put("private_property", otherAccidentForm.isPrivateProperty)


        jsonOtherForm.put("driver_name", otherAccidentForm.driverName)
        jsonOtherForm.put("driver_phoneNo", otherAccidentForm.driverPhoneNo)
        jsonOtherForm.put("driver_address", otherAccidentForm.driverAddress)
        jsonOtherForm.put("driver_city", otherAccidentForm.driverCity)
        jsonOtherForm.put("driver_state", otherAccidentForm.driverState)
        jsonOtherForm.put("driver_zipcode", otherAccidentForm.driverZipCode)


        jsonOtherForm.put("lat", otherAccidentForm.lat)
        jsonOtherForm.put("lng", otherAccidentForm.lat)

        return  jsonOtherForm

    }




    fun basicFormData():JSONObject{
        var basicAccidentForm = db1.bfaDao().getBasicFormData()

        /**
         * create json object
         * for basic form data
         */

        var jsonBasicForm = JSONObject()
//        TODO let them know about change in the below parameter
        jsonBasicForm.put("type_screen", typeBasic)
        jsonBasicForm.put("incident_type", basicAccidentForm.incidentType)
        jsonBasicForm.put("date", basicAccidentForm.date)
        jsonBasicForm.put("time", basicAccidentForm.time)
        jsonBasicForm.put("description", basicAccidentForm.description)
        jsonBasicForm.put("type", basicAccidentForm.type)
        jsonBasicForm.put("private_property", basicAccidentForm.isPrivateProperty)

        jsonBasicForm.put("driver_name", basicAccidentForm.driverName)
        jsonBasicForm.put("driver_phoneNo", basicAccidentForm.driverPhoneNo)
        jsonBasicForm.put("driver_address", basicAccidentForm.driverAddress)
        jsonBasicForm.put("driver_city", basicAccidentForm.driverCity)
        jsonBasicForm.put("driver_state", basicAccidentForm.driverState)
        jsonBasicForm.put("driver_zipcode", basicAccidentForm.driverZipCode)
        jsonBasicForm.put("lat", basicAccidentForm.lat)
        jsonBasicForm.put("lng", basicAccidentForm.lat)



        return jsonBasicForm

    }








    fun prepareDamageDetail():JSONArray{
        var jsonDamageDetailArray = JSONArray()
        var listDamageDetail = db1.damageDao().getDamageDetails()
        for (obj: DamageDetails in listDamageDetail){
            var jsonOtherMultipleVehicle = JSONObject()
            jsonOtherMultipleVehicle.put("injury_type", obj.injuryType)
            jsonOtherMultipleVehicle.put("name", obj.firstName)
            jsonOtherMultipleVehicle.put("description", obj.damageDescription)
            jsonOtherMultipleVehicle.put("address", obj.address)
            jsonOtherMultipleVehicle.put("city", obj.city)
            jsonOtherMultipleVehicle.put("state", obj.state)
            jsonOtherMultipleVehicle.put("zipcode", obj.zipcode)
            jsonOtherMultipleVehicle.put("phone_no", obj.phoneNo)
            jsonOtherMultipleVehicle.put("email", obj.email)
            jsonOtherMultipleVehicle.put("type_of_people", makeArrayOfJsonObj(obj.listTypePeople))
//            jsonOtherMultipleVehicle.put("type_of_people", )

//
// Todo               check if its correct type of people
            jsonDamageDetailArray.put(jsonOtherMultipleVehicle)
        }
        return jsonDamageDetailArray

    }



    private fun prepareWitnessDetail(): JSONArray {
        /**
         * create Json Array
         * for witness data detail
         */

        var jsonWitnessArray = JSONArray()
        var listWitnessFormData = db1.wfaDao().getWitnessFormData()
        for (obj: WitnessFormAccident in listWitnessFormData){
            var jsonWitnessObj = JSONObject()
//                jsonWitnessObj.put("driver_id", obj.driverId)
//                jsonWitnessObj.put("accident_report_id", obj.accidentReportId)
            jsonWitnessObj.put("witness_name", obj.witnessName)
            jsonWitnessObj.put("witness_phone", obj.witnessPhone)
            jsonWitnessObj.put("witness_statement", obj.witnessStatement)
            jsonWitnessObj.put("witness_audio_url", obj.witnessAudioUrl)
            jsonWitnessArray.put(jsonWitnessObj)

        }
        return jsonWitnessArray
    }



    private fun prepareForCapture(): JSONArray {
        /**
         * create Json Array
         * for capture screen
         */
        var jsonCaptureScreenArray = JSONArray()
        var listImage = db1.csaDao().getImageListss()
        for (obj: CaptureScreenAccident in listImage){
            var jsonCaptureScreen = JSONObject()
//                jsonCaptureScreen.put("driver_id", obj.driverId)
            jsonCaptureScreen.put("doc_type", obj.doc_type)
            jsonCaptureScreen.put("capture_type", obj.capture_type)
            jsonCaptureScreen.put("description", obj.description)

            jsonCaptureScreen.put("image_url", obj.image_url)
//                jsonCaptureScreen.put("accident_report_id", obj.accident_report_id)
            jsonCaptureScreenArray.put(jsonCaptureScreen)

        }
    return jsonCaptureScreenArray
    }



    private fun prepareSignReport(): JSONObject {
        var jsonSignReport = JSONObject()
        jsonSignReport.put("signImage",AppController.azureSigantureUrl)
        return jsonSignReport
    }




    fun prepareMultipleJSon():JSONArray{
        var rootJsonArray = JSONArray()

        var listMultipleVehicleList = db1.mvdDao().getVehicleDetailList()
        for (obj: MultipleVehicleDetails in listMultipleVehicleList){
//            Todo Truck
            var rootJson = JSONObject()
            var jsonArrVehicle = JSONArray()

            var jsonVehicleTruckAndInsurance = JSONObject()

            jsonVehicleTruckAndInsurance.put("type_screen",typeBasic)
            jsonVehicleTruckAndInsurance.put("vehicle_no",obj.truck_unit_no_dialog)
            jsonVehicleTruckAndInsurance.put("vehicle_year",obj.truck_year)
            jsonVehicleTruckAndInsurance.put("vehicle_make",obj.truck_make)
            jsonVehicleTruckAndInsurance.put("vehicle_license_no",obj.truck_license_no)
            jsonVehicleTruckAndInsurance.put("vehicle_license_state",obj.truck_license_state)
            jsonVehicleTruckAndInsurance.put("vehicle_type","truck")
            var insuranceObjTruck = JSONObject()

            if (obj.insuranceClaimByDriverOrTruck.equals(getString(R.string.company),ignoreCase = true)){
                if (obj.insuranceCompany.isNotEmpty()){
                    insuranceObjTruck.put("insurance_company",obj.insuranceCompany)
                    insuranceObjTruck.put("insurance_policy_no",obj.insurancePolicyNo)
                    insuranceObjTruck.put("policy_holder_phoneNo",obj.policyHolderPhoneNo)
                    insuranceObjTruck.put("policy_holder_email",obj.policyHolderEmail)
                }
            }
            jsonVehicleTruckAndInsurance.put("insurance_detail",insuranceObjTruck)
            jsonArrVehicle.put(jsonVehicleTruckAndInsurance)

//            Todo Trailer
            var jsonVehicleTrailerAndInsurance = JSONObject()
            jsonVehicleTrailerAndInsurance.put("type_screen",typeBasic)
            jsonVehicleTrailerAndInsurance.put("vehicle_no",obj.trailerNo)
            jsonVehicleTrailerAndInsurance.put("vehicle_year",obj.vehicleYear)
            jsonVehicleTrailerAndInsurance.put("vehicle_make",obj.vehicleMake)
            jsonVehicleTrailerAndInsurance.put("vehicle_license_no",obj.licenseNos)
            jsonVehicleTrailerAndInsurance.put("vehicle_license_state",obj.licenseState)
            jsonVehicleTrailerAndInsurance.put("vehicle_type","trailer")

            var insuranceObjTrailer = JSONObject()
            if (obj.insuranceClaimByDriverOrTruck.equals(getString(R.string.company),ignoreCase = true)) {
                if (obj.trailerInsuranceCompany.isNotEmpty()) {
                    insuranceObjTrailer.put("insurance_company", obj.trailerInsuranceCompany)
                    insuranceObjTrailer.put("insurance_policy_no", obj.trailerInsurancePolicyNo)
                    insuranceObjTrailer.put("policy_holder_phoneNo", obj.trailerPolicyHolderPhoneNo)
                    insuranceObjTrailer.put("policy_holder_email", obj.trailerPolicyHolderEmail)
                }
            }

            jsonVehicleTrailerAndInsurance.put("insurance_detail",insuranceObjTrailer)
            jsonArrVehicle.put(jsonVehicleTrailerAndInsurance)

            var driverInsuranceJson = JSONObject()
            if (obj.insuranceClaimByDriverOrTruck.equals(getString(R.string.driver),ignoreCase = true)) {
                driverInsuranceJson.put("insurance_company", obj.insuranceCompany)
                driverInsuranceJson.put("insurance_policy_no", obj.insurancePolicyNo)
                driverInsuranceJson.put("policy_holder_phoneNo", obj.policyHolderPhoneNo)
                driverInsuranceJson.put("policy_holder_email", obj.policyHolderEmail)
            }

            var jsonOwnerDetails = JSONObject()
            jsonOwnerDetails.put("owner_name", obj.ownerName)
            jsonOwnerDetails.put("owner_phone_no", obj.ownerPhnNo)
            jsonOwnerDetails.put("owner_address", obj.ownerAddress)
//            jsonOwnerDetails.put("owner_dob", obj.ownerDob)
            jsonOwnerDetails.put("owner_city", obj.ownerCity)
            jsonOwnerDetails.put("owner_state", obj.ownerState)
            jsonOwnerDetails.put("owner_zipcode", obj.ownerZipCode)



            rootJson.put("vehicles",jsonArrVehicle)
            rootJson.put("driver_insurance_detail",driverInsuranceJson)
            rootJson.put("owner_detail",jsonOwnerDetails)
            rootJson.put("is_vehicle_towed_away", obj.isVehicleTowedAway)
            rootJson.put("towed_company_name", obj.towedCompanyName)
            rootJson.put("towed_company_phone_no", obj.towedCompanyPhnNo)
            rootJson.put("type",typeBasic)

            rootJsonArray.put(rootJson)

        }




        var listMultipleOtherVehicleList = db1.movdDao().getOtherVehicleDetailList()
        for (obj: MultipleOtherVehicleDetails in listMultipleOtherVehicleList){

//            Todo vehicle
            var rootJson = JSONObject()
            var jsonArrVehicle = JSONArray()

            var jsonVehicleTruckAndInsurance = JSONObject()

            jsonVehicleTruckAndInsurance.put("type_screen",typeOther)
            jsonVehicleTruckAndInsurance.put("vehicle_no",obj.unitNo)
            jsonVehicleTruckAndInsurance.put("vehicle_year",obj.vehicleYear)
            jsonVehicleTruckAndInsurance.put("vehicle_make",obj.vehicleMake)
            jsonVehicleTruckAndInsurance.put("vehicle_license_no",obj.licenseNos)
            jsonVehicleTruckAndInsurance.put("vehicle_license_state",obj.licenseState)
            jsonVehicleTruckAndInsurance.put("vehicle_type","")
            var insuranceObjTruck = JSONObject()

            if (obj.isInsurance){
                insuranceObjTruck.put("insurance_company",obj.insuranceCompany)
                insuranceObjTruck.put("insurance_policy_no",obj.insurancePolicyNo)
                insuranceObjTruck.put("policy_holder_phoneNo",obj.policyHolderFirstName)
                insuranceObjTruck.put("policy_holder_email",obj.policyHolderLastName)

            }
            jsonVehicleTruckAndInsurance.put("insurance_detail",insuranceObjTruck)
            jsonArrVehicle.put(jsonVehicleTruckAndInsurance)




            var driverInsuranceJson = JSONObject()
            if (obj.isInsurance) {
                driverInsuranceJson.put("insurance_company", obj.insuranceCompany)
                driverInsuranceJson.put("insurance_policy_no", obj.insurancePolicyNo)
                driverInsuranceJson.put("policy_holder_phoneNo", obj.policyHolderFirstName)
                driverInsuranceJson.put("policy_holder_email", obj.policyHolderLastName)
            }



            var jsonOwnerDetails = JSONObject()
//            jsonOwnerDetails.put("owner_name", obj.ownerName)
//            jsonOwnerDetails.put("owner_last_name", obj.ownerAddress)
//            jsonOwnerDetails.put("owner_phone_no", obj.owner_phone_no)
//            jsonOwnerDetails.put("owner_dob", obj.owner_)
//            jsonOwnerDetails.put("owner_city", obj.owner_city)
//            jsonOwnerDetails.put("owner_state", obj.owner_state)
//            jsonOwnerDetails.put("owner_zipcode", obj.owner_zipcode)

            jsonOwnerDetails.put("owner_name", obj.owner_first_name)
            jsonOwnerDetails.put("owner_phone_no", obj.owner_phone_no)
            jsonOwnerDetails.put("owner_address", obj.owner_address)
            jsonOwnerDetails.put("owner_city", obj.owner_city)
            jsonOwnerDetails.put("owner_state", obj.owner_state)
            jsonOwnerDetails.put("owner_zipcode", obj.owner_zipcode)






            rootJson.put("vehicles",jsonArrVehicle)
            rootJson.put("driver_insurance_detail",driverInsuranceJson)
            rootJson.put("owner_detail",jsonOwnerDetails)
            rootJson.put("is_vehicle_towed_away", obj.isVehicleTowedAway)
            rootJson.put("towed_company_name", obj.towedCompanyName)
            rootJson.put("towed_company_phone_no", obj.towedCompanyPhnNo)
            rootJson.put("type",typeOther)

            rootJsonArray.put(rootJson)



        }


return  rootJsonArray







    }




    fun makeArrayOfJsonObj(arrOfStr:ArrayList<String>):JSONArray{
        var jsonArr = JSONArray()
        for (str in arrOfStr) {
            jsonArr.put(str)
        }
        return jsonArr
    }

    fun removeSpecialCharFrmString(str : String):String{
      var reqStr =  str.replace("[]","")
        return reqStr
    }


    @Throws(Exception::class)
    private fun getJsonContainer(container_name: String): CloudBlobContainer? {
        // Retrieve storage account from connection-string.
        val storageAccount = CloudStorageAccount
                .parse(Utils.storageConnectionString)

        // Create the blob client.
        val blobClient = storageAccount.createCloudBlobClient()

        // Get a reference to a container.
        // The container name must be lower case
        return blobClient.getContainerReference(container_name)
    }

//        public static void UploadImageFiles(HashMap<String, File> files) throws Exception {
//        CloudBlobContainer container = getJsonContainer("images");
//
//        container.createIfNotExists();
//        //container.setMetadata(meta_data);
//        Iterator it = files.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry entry = (Map.Entry) it.next();
//            CloudBlockBlob imageBlob = container.getBlockBlobReference(entry.getKey().toString());
//
//            File file = (File) entry.getValue();
//            String file_uri = imageBlob.getUri().toString();
//            url_arrayList.add(file_uri);
//            Log.e("uri", " is " + imageBlob.getUri());
//
//            imageBlob.uploadFromFile(file.getAbsolutePath());
//
//            it.remove(); // avoids a ConcurrentModificationException
//        }
//
//    }


    var azure_file_hashmap = HashMap<String, File>()

    var files_array = java.util.ArrayList<File>()

    fun prepareListToUploadFiles(list:ArrayList<String>):HashMap<String, File>{
        for (cn in list) {
            try {
                var temp_file = File(cn)
                files_array.add(temp_file)
                var temp_name = ""
                if (cn.contains("jpg"))
                temp_name = ("Accident" + "-"
                        + Date().toString() + "-"
                        +  ".jpg")
                else
                    temp_name = ("Accident" + "-"
                            + "123456"
                            +".mp4")
                azure_file_hashmap[temp_name] = temp_file

            } catch (e: java.lang.Exception) {
                Log.e("error",": $")
            }
        }
        return azure_file_hashmap
    }


    fun uploadImageAndAudioFile(files:HashMap<String,File>){
        var container = getJsonContainer("images")
        container?.createIfNotExists()

        var it = files.entries.iterator()

        while (it.hasNext()){
            var entry = it.next()
            val imageBlob = container!!.getBlockBlobReference(entry.key)

            val file = entry.value
            val file_uri = imageBlob.uri.toString()
            Log.e("uri", " is " + file_uri)

//            url_arrayList.add(file_uri)
            imageBlob.uploadFromFile(entry.value.absolutePath)
            it.remove()
        }

    }





    private fun addONeImage(){
        CoroutineScope(IO).launch {
            val container = getJsonContainer("images")
            container?.createIfNotExists()
            val imageBlob = container?.getBlockBlobReference("helloe123456.jpg")
            var file = File("/storage/emulated/0/tmsfalcon/TMSFALCON-20201201124952.jpg")
            imageBlob?.uploadFromFile(file.absolutePath)
            Log.e("succesful","succesful")
        }
    }

    private fun settingImage(){
        if(networkValidator!!.isNetworkConnected) {
            progress_bar.visibility = View.VISIBLE
            CoroutineScope(IO).launch {
//                val container = getJsonContainer("images")
                val container = getJsonContainer(AccidentContainerName)
                container?.createIfNotExists()
                fetchImageVideoUpdate(container)
                fetchSignatureImage(container)
//                val container1 = getJsonContainer("traveloko-documents")

                val container1 = getJsonContainer(AccidentContainerName)
                container?.createIfNotExists()
                fetchSoundUpdate(container1)
//                makeJson()
                makeNewJson()
            }
        }else {
            Toast.makeText(this@KotAccidentFinalScreen,resources.getString(R.string.network_error),Toast.LENGTH_SHORT).show()
        }
    }



    fun fetchSignatureImage(container:CloudBlobContainer?){
        val imageBlob = container?.getBlockBlobReference(namingImageVideo("Signature",false))

        var bmp = AppController.signatureAccident;
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        imageBlob?.uploadFromByteArray(byteArray,0,byteArray.size)
        Log.e("uploaded","UploadedSignature")
        bmp.recycle()
        AppController.signatureAccident.recycle()
        AppController.azureSigantureUrl = imageBlob?.uri.toString()
    }

    /**
     * Fetch uri from db and upload to server
     * and update the server uri to local database
     */

    fun fetchImageVideoUpdate(container:CloudBlobContainer?){
        var list =  db1.csaDao().getImageListss()
        for (obj in list){
            val imageBlob = container?.getBlockBlobReference(namingImageVideo("Capture",obj.isItVideo))
            if(obj.image_url.isNotEmpty()) {
                var file = File(obj.image_url)
                imageBlob?.uploadFromFile(file.absolutePath)
            }   
            obj.image_url = imageBlob?.uri.toString()
            db1.csaDao().updateImageData(obj)
        }
    }

    /**
     * create unique image name
     */

    fun namingImageVideo(name:String,isItVideo:Boolean):String{
        var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        var value = simpleDateFormat.format(Date())
        var finalStr:String
        if (isItVideo){
            finalStr = "$name$value.mp4"
        }else {
            finalStr = "$name$value.jpg"
        }
        return finalStr
    }





    /**
     * Fetch uri from db and upload to server
     * and update the server uri to local database
     */

    fun fetchSoundUpdate(container:CloudBlobContainer?){
        var list =  db1.wfaDao().getWitnessFormData()
        for (obj in list){
            val imageBlob = container?.getBlockBlobReference(namingSound())
            if (obj.witnessAudioUrl.isNotEmpty()) {
                var file = File(obj.witnessAudioUrl)
                imageBlob?.uploadFromFile(file.absolutePath)
            }
            obj.witnessAudioUrl = imageBlob?.uri.toString()
            db1.wfaDao().updateAccident(obj)
        }
    }

    /**
     * create unique sound name
     */

    fun namingSound():String{
        var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        var value = simpleDateFormat.format(Date())
        var finalStr = "Sound$value.mp4"
        return finalStr
        //"Capture"
    }

    private fun sendData() {
        Log.e("in", "sendData")
        val accidentRecordsRequest = AccidentRecordsRequest()
        var accident_report_id = 0
        var accidentWitnessModelArrayList = ArrayList<AccidentWitnessModel>()
        var accidentVehicleDetailsModelArrayList = ArrayList<AccidentVehicleDetailsModel>()
        var witness_audio_hashmap: MutableMap<String?, File?>? = null
        val vehicle_json_array = JSONArray()
        val witness_json_array = JSONArray()
        val accident_json_object = JSONObject()
        if (db!!.checkIfAccidentTableExists() && !db!!.checkAccidentBasicDetailEmptyTable()) {
            Log.e("in", "checkIfAccidentTableExists")
            val accidentBasicDetailsModel = db!!.getAccidentBasicDetailById(AppController.accident_report_id)
            accidentRecordsRequest.accidentBasicDetailsModel = accidentBasicDetailsModel
            try {
                accident_json_object.put("accident_date", accidentBasicDetailsModel.accident_date)
                accident_json_object.put("accident_time", accidentBasicDetailsModel.accident_time)
                accident_json_object.put("accident_location", accidentBasicDetailsModel.accident_location)
                accident_json_object.put("employer_name", accidentBasicDetailsModel.employer_name)
                accident_json_object.put("employer_phone_number", accidentBasicDetailsModel.employer_phone_number)
                accident_json_object.put("accident_lat", accidentBasicDetailsModel.accident_lat)
                accident_json_object.put("accident_lng", accidentBasicDetailsModel.accident_long)
                accident_json_object.put("employer_insurance_provider", accidentBasicDetailsModel.employer_insurance_provider)
                accident_json_object.put("employer_insurance_policy_no", accidentBasicDetailsModel.employer_insurance_policy_number)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            accident_report_id = accidentBasicDetailsModel.id
            if (db!!.checkIfVehicleTableExists() && !db!!.checkVehicleBasicDetailEmptyTable()) {
                Log.e("in", "checkIfVehicleTableExists")
                accidentVehicleDetailsModelArrayList = db!!.getVehicleDetailById(accident_report_id)
                accidentRecordsRequest.accidentVehicleDetailsModelArrayList = accidentVehicleDetailsModelArrayList
                for (model in accidentVehicleDetailsModelArrayList) {
                    val vehicle_json_object = JSONObject()
                    try {
                        vehicle_json_object.put("vehicle_insurance_provider", model.vehicle_insurance_provider)
                        vehicle_json_object.put("vehicle_insurance_policy_number", model.vehicle_insurance_policy_number)
                        vehicle_json_object.put("dot_number", model.vehicle_dot_number)
                        vehicle_json_object.put("license_plate_number", model.vehicle_license_number)
                        vehicle_json_object.put("vehicle_registration", model.vehicle_registration_number)
                        vehicle_json_object.put("vo_name", model.vehicle_owner_name)
                        vehicle_json_object.put("vo_phone_number", model.vehicle_owner_phone_number)
                        vehicle_json_object.put("vo_insurance_provider", model.vehicle_owner_insurance_provider)
                        vehicle_json_object.put("vo_insurance_policy_number", model.vehicle_owner_insurance_policy_number)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    vehicle_json_array.put(vehicle_json_object)
                    //vehicleDetails.add(vehicle_hashmap);
                }
            }
            if (witnessTable!!.checkIfTableExists() && !witnessTable!!.checkWitnessEmptyTable()) {
                Log.e("in", "checkIfTableExists")
                accidentWitnessModelArrayList = witnessTable!!.getWitnessDetailById(accident_report_id)
                accidentRecordsRequest.accidentWitnessModelArrayList = accidentWitnessModelArrayList
                for (model in accidentWitnessModelArrayList) {
                    val witness_json_object = JSONObject()
                    try {
                        witness_json_object.put("witness_name", model.witness_name)
                        witness_json_object.put("witness_phone", model.witness_phone)
                        witness_json_object.put("witness_statement", model.witness_statement)
                        if (model.witness_audio_url != null && model.witness_audio_url.length > 0) {
                            witness_audio_hashmap = HashMap()
                            val file = File(model.witness_audio_url)
                            val timeStamp = SimpleDateFormat("yyyyMMddHHmmss")
                                    .format(Date())
                            witness_json_object.put("temp_name", "witness-$timeStamp")
                            witness_audio_hashmap["witness-$timeStamp"] = file
                        } else {
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    witness_json_array.put(witness_json_object)
                }
            }
            Log.e("in", "JSONObject")
            val data_json = JSONObject()
            try {
                data_json.put("accident_details", accident_json_object)
                data_json.put("vehicle_details", vehicle_json_array)
                data_json.put("witness_details", witness_json_array)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Log.e("data_json", data_json.toString())
//            accident_progress_layout!!.visibility = View.VISIBLE
            progress_bar!!.progress = 0
//            photos_progress_bar!!.progress = 0
            val finalAccident_report_id = accident_report_id
//            val getRequestBuilder: MultiPartBuilder<*> = MultiPartBuilder(UrlController.ACCIDENT_DETAIL)

            val getRequestBuilder: MultiPartBuilder<*> = MultiPartBuilder<ANRequest.MultiPartBuilder<ANRequest.MultiPartBuilder<*>>>(UrlController.ACCIDENT_DETAIL)

            if (witness_audio_hashmap != null) {
                Log.e("witness_audio_hashmap", "not null")
                getRequestBuilder.addMultipartFile(witness_audio_hashmap)
            } else {
                Log.e("witness_audio_hashmap", "null")
            }
            getRequestBuilder.addHeaders("Token", session!!._token)
            getRequestBuilder.addMultipartParameter("data", data_json.toString())
            val anRequest = getRequestBuilder.build()
            anRequest.setUploadProgressListener { bytesUploaded, totalBytes ->
                val percentage = (bytesUploaded * 100 / totalBytes).toInt()

                // int percentage = (int)(100 * bytesUploaded / totalBytes);
                progress_bar!!.progress = percentage
//                progress_bar!!.setText("$percentage %")
            }
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            // accident_progress_layout.setVisibility(View.GONE);
                            Log.e("new response", " is $response")
                            try {
                                val status = response.getBoolean("status")
                                if (status) {
//                                    accident_data_status!!.visibility = View.VISIBLE
//                                    accident_data_status!!.text = "Uploaded"
//                                    accident_data_status!!.setTextColor(resources.getColor(R.color.green_dark))
                                    val server_accident_id = response.getInt("accident_id")
                                    if (document_db!!.checkIfTableExists() && !document_db!!.checkAccidentDocumentEmptyTable()) {
                                        sendDocuments(finalAccident_report_id, server_accident_id)
                                    } else {
                                        var witnessModelList = ArrayList<AccidentWitnessModel>()
                                        witnessModelList = witnessTable!!.getWitnessDetailById(finalAccident_report_id)
                                        for (model in witnessModelList) {
                                            val audio_url = model.witness_audio_url
                                            if (audio_url != null || audio_url !== "") {
                                                val file = File(audio_url)
                                                if (file.exists()) {
                                                    file.delete()
                                                }
                                            }
                                        }

                                        // delete data from sqlite here
                                        AppController.accident_report_id = 0
                                        db!!.delAccidentBasicById(finalAccident_report_id.toString())
                                        witnessTable!!.deleteWitnessById(finalAccident_report_id)
                                        db!!.delVehicleBasicById(finalAccident_report_id.toString())
                                    }
                                } else {
//                                    accident_data_status!!.visibility = View.VISIBLE
//                                    accident_data_status!!.text = "Failed"
//                                    accident_data_status!!.setTextColor(resources.getColor(R.color.red_dark))
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onError(anError: ANError) {
//                            accident_progress_layout!!.visibility = View.GONE
                            val error_body = anError.errorBody
                            var msg = ""
                            msg = error_body ?: "Some Error Occured.Please Try Again Later!"
//                            accident_data_status!!.visibility = View.VISIBLE
//                            accident_data_status!!.text = "Failed"
//                            accident_data_status!!.setTextColor(resources.getColor(R.color.red_dark))
                            Toast.makeText(this@KotAccidentFinalScreen, msg, Toast.LENGTH_LONG).show()
                            Log.e("new error", " is " + anError.errorBody)
                        }
                    })
        }
    }

    private fun sendDocuments(accident_report_id: Int, server_accident_id: Int) {
        var accidentCaptureModelArrayList = ArrayList<AccidentCaptureModel>()
        document_hashmap = HashMap()
        val document_data_array = JSONArray()
        if (document_db!!.checkIfTableExists() && !document_db!!.checkAccidentDocumentEmptyTable()) {
            accidentCaptureModelArrayList = document_db!!.getAllAccidentImagesById(accident_report_id)
            for (model in accidentCaptureModelArrayList) {
                val document_json_object = JSONObject()
                try {
                    document_json_object.put("accident_id", server_accident_id)
                    document_json_object.put("doc_type", model.doc_type)
                    val file = File(model.image_url)
                    val timeStamp = SimpleDateFormat("yyyyMMddHHmmss")
                            .format(Date())
                    document_json_object.put("temp_name", "document-$timeStamp")
                    document_hashmap["document-$timeStamp"] = file
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                document_data_array.put(document_json_object)
            }
//            photos_progress_layout!!.visibility = View.VISIBLE
//            photos_progress_bar!!.progress = 0
            val getRequestBuilder: MultiPartBuilder<*> = MultiPartBuilder<ANRequest.MultiPartBuilder<ANRequest.MultiPartBuilder<*>>>(UrlController.ACCIDENT_DOCUMENTS)
            if (document_hashmap != null) {
                getRequestBuilder.addMultipartFile(document_hashmap)
            } else {
                Log.e("witness_audio_hashmap", "null")
            }
            getRequestBuilder.addHeaders("Token", session!!._token)
            if (document_data_array != null && document_data_array.length() > 0) {
                getRequestBuilder.addMultipartParameter("data", document_data_array.toString())
            } else {
                getRequestBuilder.addMultipartParameter("data", "")
            }
            val anRequest = getRequestBuilder.build()
            anRequest.setUploadProgressListener { bytesUploaded, totalBytes ->
                val percentage = (bytesUploaded * 100 / totalBytes).toInt()
                // int percentage = (int)(100 * bytesUploaded / totalBytes);
//                photos_progress_bar!!.progress = percentage
//                photos_progress_bar!!.setText("$percentage %")
            }
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            // accident_progress_layout.setVisibility(View.GONE);
                            Log.e("new response", " is $response")
                            try {
                                val status = response.getBoolean("status")
                                if (status) {
                                    var witnessModelList = ArrayList<AccidentWitnessModel>()
                                    witnessModelList = witnessTable!!.getWitnessDetailById(accident_report_id)
                                    for (model in witnessModelList) {
                                        val audio_url = model.witness_audio_url
                                        if (audio_url != null || audio_url !== "") {
                                            val file = File(audio_url)
                                            if (file.exists()) {
                                                file.delete()
                                            }
                                        }
                                    }
                                    var accidentCaptureModelArrayList = ArrayList<AccidentCaptureModel>()
                                    accidentCaptureModelArrayList = document_db!!.getAllAccidentImagesById(accident_report_id)
                                    for (model in accidentCaptureModelArrayList) {
                                        val file_url = model.image_url
                                        if (file_url != null || file_url !== "") {
                                            val file = File(file_url)
                                            if (file.exists()) {
                                                file.delete()
                                            }
                                        }
                                    }

                                    // delete data from sqlite here
                                    AppController.accident_report_id = 0
                                    db!!.delAccidentBasicById(accident_report_id.toString())
                                    witnessTable!!.deleteWitnessById(accident_report_id)
                                    db!!.delVehicleBasicById(accident_report_id.toString())
                                    document_db!!.deleteImagesByAccidentId(accident_report_id.toString())
//                                    photos_status!!.visibility = View.VISIBLE
//                                    photos_status!!.text = "Uploaded"
//                                    photos_status!!.setTextColor(resources.getColor(R.color.green_dark))
//                                    report_status_textview!!.visibility = View.VISIBLE
//                                    report_status_textview!!.setTextColor(resources.getColor(R.color.green_dark))
//                                    report_status_textview!!.text = resources.getString(R.string.accident_report_uploaded_successfully)

                                    // delete data from sqlite here
                                    AppController.accident_report_id = 0
                                    //  db.delAccidentBasicById(String.valueOf(accident_report_id));
                                    // db.delVehicleBasicById(String.valueOf(accident_report_id));
                                    // document_db.deleteImagesById(String.valueOf(accident_report_id));
                                } else {
//                                    photos_status!!.visibility = View.VISIBLE
//                                    photos_status!!.text = "Failed"
//                                    photos_status!!.setTextColor(resources.getColor(R.color.red_dark))
//                                    report_status_textview!!.visibility = View.VISIBLE
//                                    report_status_textview!!.setTextColor(resources.getColor(R.color.red_dark))
//                                    report_status_textview!!.text = resources.getString(R.string.accident_report_upload_failed)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }

                        override fun onError(anError: ANError) {
//                            photos_progress_layout!!.visibility = View.GONE
//                            photos_status!!.visibility = View.VISIBLE
//                            photos_status!!.text = "Failed"
//                            photos_status!!.setTextColor(resources.getColor(R.color.red_dark))
                            val error_body = anError.errorBody
                            var msg = ""
                            msg = error_body ?: "Some Error Occured.Please Try Again Later!"
//                            accident_data_status!!.visibility = View.VISIBLE
//                            accident_data_status!!.text = "Failed"
//                            accident_data_status!!.setTextColor(resources.getColor(R.color.red_dark))
                            Toast.makeText(this@KotAccidentFinalScreen, msg, Toast.LENGTH_LONG).show()
                            Log.e("new error", " is " + anError.errorBody)
                        }
                    })
        }
    }



    @OnClick(R.id.upload_now)
    fun uploadNow() {

        AlertDialog.Builder(this@KotAccidentFinalScreen,R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Upload!!")
                .setMessage("Are you sure you want to upload the data? After uploading you will not be able to edit it!") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(R.string.yes_its_final, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.dismiss()
                        settingImage()

                    }
                }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.not_sure, object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()




//        addONeImage()
//        makeJson()
//        uploadImagesFor()
//        if (networkValidator!!.isNetworkConnected) {
//            sendData()
////            Log.e("in", "upload now click")
////            /* AccidentModuleCalls accidentModuleCalls = new AccidentModuleCalls();
////            accidentModuleCalls.sendAccidentRequest(AccidentFinalScreen.this);*/
////            //sendDataToServer();
//        } else {
//            report_status_textview!!.text = resources.getString(R.string.internet_not_connected)
//            report_status_textview!!.setTextColor(resources.getColor(R.color.red_dark))
//            report_status_textview!!.visibility = View.VISIBLE
//        }

    }

//    @OnClick(R.id.upload_later)
//    fun uploadLater() {
//        AppController.LOAD_ACCIDENT_DATA = true
////        val msg = "Data will be uploaded automatically whenever phone has internet connection."
////        //Toast.makeText(AccidentFinalScreen.this,msg,Toast.LENGTH_LONG).show();
////        val goTolist = Intent(this@KotAccidentFinalScreen, OfflineReportedAccident::class.java)
////        startActivity(goTolist)
////
//
//
//        navigateToRespectiveScreen()
//
//
//    }

    fun navigateToRespectiveScreen(){
        val returnIntent = Intent()
        returnIntent.putExtra("result", "result")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()

    }


    fun replaceUnReqText(str:String):String{
        var result = str.replace("Trailer No:","")
        return result.trim()
    }





    companion object {
        fun requestBody(name: String?): RequestBody {
            return RequestBody.create(MediaType.parse("t" +
                    "" +
                    "" +
                    "" +
                    "ext/plain"), name)
        }

        fun audioToRequestBody(file: File?): RequestBody { //for image file to request body
            return RequestBody.create(MediaType.parse("audio/*"), file)
        }

        fun stringToRequestBody(string: String?): RequestBody { // for string to request body
            return RequestBody.create(MediaType.parse("text/plain"), string)
        }
    }
}