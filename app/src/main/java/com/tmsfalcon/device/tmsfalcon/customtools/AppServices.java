
package com.tmsfalcon.device.tmsfalcon.customtools;

import com.tmsfalcon.device.tmsfalcon.Responses.ContactsDataResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.DayOffResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.EmailSuggestionResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.EscrowDetailResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.FetchDispatcherResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.GpsDataResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.LoanInstallmentFragmenResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.PostDataResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.ProfilePicUploadResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.TripDetailResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.TripResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.VoicemailDataResponse;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentRecordsRequest;
import com.tmsfalcon.device.tmsfalcon.entities.LoanDetailModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by zabius on 9/8/17.
 */

public interface AppServices {


//    @GET("http://maps.googleapis.com/maps/api/geocode/json?")
//    Call<GoogleAddressResponse> getGoogleAddress(@Query("address") String addressId);

//    @GET("https://maps.googleapis.com/maps/api/place/autocomplete/json?")
//    Call<GooglePlaceApiResponse> checkGooglePlaceApi(@Query("key") String addkeyressId,
//                                                     @Query("input") String input);
//
    @Multipart
    @POST("document_upload")
    Call<PostResponse> uploadDocument(@Header("token") String token,
                                      @Part List<MultipartBody.Part> multiImages,
                                      @PartMap Map<String, RequestBody> field);

    @Multipart
    @POST("upload_profile_picture")
    Call<ProfilePicUploadResponse> uploadProfilePic(@Header("token") String token,
                                                    @Part MultipartBody.Part multiImages);

    @Multipart
    @POST("upload_profile_picture_resource")
    Call<ProfilePicUploadResponse> uploadResourceProfilePic(@Header("token") String token,
                                                    @Part MultipartBody.Part multiImages,
                                                    @PartMap Map<String, RequestBody> field);

    @Multipart
    @POST("send_email")
    Call<PostResponse> emailDocuments(@Header("token") String token,
                                      @Part List<MultipartBody.Part> multiImages,
                                      @PartMap Map<String, RequestBody> field);


    /*@Multipart
    @POST("gps_location")
    Call<GpsDataResponse> sendGpsLocationParams(@Header("token") String token,
                                      @PartMap Map<String, RequestBody> field);*/

   /* @Multipart
    @POST("gps_location")
    Call<GpsDataResponse> sendGpsLocationParams(@Header("token") String token,
                                                @PartMap HashMap<String,ArrayList<HashMap<String, String>>> field);*/

    @POST("gps_location")
    Call<GpsDataResponse> sendGpsLocationParams(@Header("token") String token,
                                                @Body RequestBody body);

    @Multipart
    @POST("loan_installments")
    Call<LoanInstallmentFragmenResponse> getLoanInstallments(@Header("token") String token,
                                                             @PartMap Map<String, RequestBody> field);

    @FormUrlEncoded
    @POST("accident_detail")
    Call<PostResponse> postAccidentDetails(@Header("token") String token,
                                               @Field("accident_details") Map<String, String> accidentDetails,
                                               @Field("vehicle_details") ArrayList<Map<String, String>> vehicleDetails,
                                               @Field("witness_details") ArrayList<Map<String, RequestBody>> witnessDetails);

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    @GET("email_suggestions")
    Call<EmailSuggestionResponse> fetchEmailSuggestions(@Header("token") String token);

    @GET("get_dispatchers")
    Call<FetchDispatcherResponse> fetchDispatchersEmail(@Header("token") String token);

    @GET("interval_settings")
    Call<GpsDataResponse> fetchGpsData(@Header("token") String token);

    @GET("contact_information")
    Call<ContactsDataResponse> fetchContactInformation(@Header("token") String token);

    @GET("fetch_user_voicemails")
    Call<VoicemailDataResponse> fetchUserVoicemails(@Header("token") String token);

    @GET("trip/?")
    Call<TripDetailResponse> fetchTripDetail(@Header("token") String token,@Query("trip_id") int trip_id);

    @GET("loan_detail/?")
    Call<LoanDetailModel> fetchLoanDetail(@Header("token") String token, @Query("loan_id") int loan_id);

    @GET("escrow_detail/?")
    Call<EscrowDetailResponse> fetchEscrowDetail(@Header("token") String token, @Query("escrow_id") int escrow_id);

    @FormUrlEncoded
    @POST("delete_voicemail")
    Call<PostResponse> deleteUserVoicemail(@Header("token") String token,@Field("message_id") String message_id);

    @FormUrlEncoded
    @POST("update_voicemail_status")
    Call<PostResponse> updateVoicemailReadStatus(@Header("token") String token,@Field("message_id") String message_id,@Field("read_status") String read_status);

    @FormUrlEncoded
    @POST("get_day_off")
    Call<DayOffResponse> fetchDayOff(@Header("token") String token,@Field("offset") int offset);

    @FormUrlEncoded
    @POST("upcoming_trips")
    Call<PostResponse> fetchUpcomingTrips(@Header("token") String token, @Field("offset") int offset);

    @Multipart
    @POST("set_password")
    Call<PostResponse> setPassword(@Header("token") String token,
                                   @PartMap Map<String, RequestBody> field);

    @Multipart
    @POST("send_ringcentral_email")
    Call<PostResponse> emailDocumentsForRc(@Header("token") String token,
                                      @PartMap Map<String, RequestBody> field);

    @Multipart
    @POST("send_feedback_email")
    Call<PostResponse> feedBackEmail(@Header("token") String token,
                                           @PartMap Map<String, RequestBody> field);

    @Multipart
    @POST("send_url_email")
    Call<PostResponse> emaiUrl(@Header("token") String token,
                                           @PartMap Map<String, RequestBody> field);
//
//    @FormUrlEncoded
//    @POST("verify_otp")
//    Call<PostResponse> verifyOtp(@Field("otp_text") String otp_text,
//                                 @Field("token") String token);

//    @FormUrlEncoded
//    @POST("login")
//    Call<UserResponse> login(@Field("username") String username,
//                             @Field("password") String password,
//                             @Field("userType") String userType);
//
//    @FormUrlEncoded
//    @POST("updateotp")
//    Call<UserResponse> updateOtp(@Field("token") String token,
//                                 @Field("email") String username,
//                                 @Field("id") String password);
//
//    @GET("getAllCategories")
//    Call<CategoryResponse> getAllCategories(@Header("token") String token,@Query("af") String a);
//
//    @FormUrlEncoded
//    @POST("getCategoryWisedGroups")
//    Call<GroupListResponse> getGroupList(@Header("token") String token,
//                                         @FieldMap Map<String, String> field);
//
//
//    @GET("getCategories")
//    Call<CategoryResponse> getCategory1(@Header("token") String token);
//
//    @FormUrlEncoded
////    @POST("getAllSellerAdvertisement")
////    Call<SellerAdvertisementResponse> getAllSellerAdvertisement(@Header("token") String token,
//                                                                @FieldMap Map<String, String> field);
//
//    @FormUrlEncoded
//    @POST("getAllAdvertisements")
//    Call<SellerAdvertisementResponse> getAllUserAdvertisement(@Header("token") String token,
//                                                              @FieldMap Map<String, String> field);
//    @FormUrlEncoded
//    @POST("getAdvertisement")
//    Call<SellerAdvertisementResponse> getAdvertisement(@Header("token") String token,
//                                                              @Field("advertisement_id") String field);
//
//    @FormUrlEncoded
//    @POST("getSubcategories")
//    Call<CategoryResponse> getCategory2(@Header("token") String token,
//                                        @Field("category_id") String category_id);
//
//    @FormUrlEncoded
//    @POST("getSubcategories2")
//    Call<CategoryResponse> getCategory3(@Header("token") String token,
//                                        @Field("subcategory_id") String subcategory_id2);
//
//    @FormUrlEncoded
//    @POST("getSubcategories3")
//    Call<CategoryResponse> getCategory4(@Header("token") String token,
//                                        @Field("subcategory_id2") String subcategory_id3);
//
//    @FormUrlEncoded
//    @POST("getSubcategories4")
//    Call<CategoryResponse> getCategory5(@Header("token") String token,
//                                        @Field("subcategory_id3") String category_id);
//
//    @FormUrlEncoded
//    @POST("getSubcategories5")
//    Call<CategoryResponse> getCategory6(@Header("token") String token,
//                                        @Field("subcategory_id4") String category_id);
//
//    @Multipart
//    @POST("createAdvertisement")
//    Call<PostResponse> createAdvertisement(@Header("token") String token,
//                                           @Part List<MultipartBody.Part> multiImages,
//                                           @PartMap Map<String, RequestBody> field);
//
//    @Multipart
//    @POST("createGroup")
//    Call<PostResponse> createGroup(@Header("token") String token,
//                                   @Part MultipartBody.Part multiImages,
//                                   @PartMap Map<String, RequestBody> field);
//
//    @Multipart
//    @POST("editProfile")
//    Call<UserResponse> editProfile(@Header("token") String token,
//                                   @Part List<MultipartBody.Part> multiImages,
//                                   @PartMap Map<String, RequestBody> field);
//
//    @GET("profile")
//    Call<UserResponse> getProfile(@Header("token") String token);
//
//    @FormUrlEncoded
//    @POST("getGroupDetails")
//    Call<GroupDetailResponse> getGroupDetails(@Header("token") String token,
//                                              @Field("category_id") String category_id,
//                                              @Field("group_id") String group_id);
//
//    @FormUrlEncoded
//    @POST("joinGroup")
//    Call<PostResponse> joinGroup(@Header("token") String token,
//                                 @Field("group_id") String group_id);
//
//    @FormUrlEncoded
//    @POST("exitGroup")
//    Call<PostResponse> exitGroup(@Header("token") String token,
//                                 @Field("group_id") String group_id);
//
//    @FormUrlEncoded
//    @POST("report")
//    Call<PostResponse> report(@Header("token") String token,
//                              @Field("comments") String comments,
//                              @Field("reportTo") String reportTo,
//                              @Field("report_type") String report_type);
//
//    @FormUrlEncoded
//    @POST("forgotPassword")
//    Call<PostResponse> forgotPassword(
//            @Field("username") String emailId);
//
//    @FormUrlEncoded
//    @POST("changeSetting")
//    Call<PostResponse> updateNotification(@Header("token") String token,
//                                          @Field("notify_status") String notify_status);
//
//    @FormUrlEncoded
//    @POST("changePassword")
//    Call<PostResponse> changePassword(@Header("token") String token,
//                                      @Field("old_password") String old_password,
//                                      @Field("new_password") String new_password,
//                                      @Field("confirm_new_password") String confirm_new_password);
//
//    @GET("getCreateGroups")
//    Call<GroupListResponse> getCreatedGroups(@Header("token") String token);
//
//    @GET("getJoinedGroups")
//    Call<GroupListResponse> getJoinedGroups(@Header("token") String token);
//
//
//    @FormUrlEncoded
//    @POST("groupMemberDetail")
//    Call<MemberDetailResponse> getMemberDetail(@Header("token") String token,
//                                               @Field("member_id") String member_id,
//                                               @Field("group_id") String group_id);
}
