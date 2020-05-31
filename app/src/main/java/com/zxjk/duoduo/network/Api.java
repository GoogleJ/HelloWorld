package com.zxjk.duoduo.network;

import com.zxjk.duoduo.bean.AuditCertificationBean;
import com.zxjk.duoduo.bean.CardBackBean;
import com.zxjk.duoduo.bean.CardFaceBean;
import com.zxjk.duoduo.bean.response.AirdropInfoResponse;
import com.zxjk.duoduo.bean.response.AllGroupMembersResponse;
import com.zxjk.duoduo.bean.response.AssetManageBean;
import com.zxjk.duoduo.bean.response.BalanceAssetManageBean;
import com.zxjk.duoduo.bean.response.BaseResponse;
import com.zxjk.duoduo.bean.response.BlockChainNewsBean;
import com.zxjk.duoduo.bean.response.ByBoinsResponse;
import com.zxjk.duoduo.bean.response.CastListBean;
import com.zxjk.duoduo.bean.response.CommunityApplicationListResponse;
import com.zxjk.duoduo.bean.response.CommunityCultureResponse;
import com.zxjk.duoduo.bean.response.CommunityFilesListResponse;
import com.zxjk.duoduo.bean.response.CommunityInfoResponse;
import com.zxjk.duoduo.bean.response.CommunityListBean;
import com.zxjk.duoduo.bean.response.CommunityVideoListResponse;
import com.zxjk.duoduo.bean.response.CurrencyInfosByCustomerBean;
import com.zxjk.duoduo.bean.response.EditCommunityResponse;
import com.zxjk.duoduo.bean.response.EditListCommunityCultureResponse;
import com.zxjk.duoduo.bean.response.FindHailangResponse;
import com.zxjk.duoduo.bean.response.FriendInfoResponse;
import com.zxjk.duoduo.bean.response.GenerateMnemonicResponse;
import com.zxjk.duoduo.bean.response.GetAppVersionResponse;
import com.zxjk.duoduo.bean.response.GetBalanceInfoResponse;
import com.zxjk.duoduo.bean.response.GetCarouselMap;
import com.zxjk.duoduo.bean.response.GetChatRoomInfoResponse;
import com.zxjk.duoduo.bean.response.GetCustomerBasicInfoByIdResponse;
import com.zxjk.duoduo.bean.response.GetFriendsByMobilesResponse;
import com.zxjk.duoduo.bean.response.GetGroupChatInfoByGroupIdResponse;
import com.zxjk.duoduo.bean.response.GetGroupPayInfoResponse;
import com.zxjk.duoduo.bean.response.GetGroupRedPackageInfoResponse;
import com.zxjk.duoduo.bean.response.GetInviteInfoResponse;
import com.zxjk.duoduo.bean.response.GetLiveInfoByGroupIdResponse;
import com.zxjk.duoduo.bean.response.GetMainSymbolByCustomerIdBean;
import com.zxjk.duoduo.bean.response.GetOrderInfoByTypeResponse;
import com.zxjk.duoduo.bean.response.GetParentSymbolBean;
import com.zxjk.duoduo.bean.response.GetPaymentListBean;
import com.zxjk.duoduo.bean.response.GetRecommendCommunity;
import com.zxjk.duoduo.bean.response.GetRedNewPersonInfoResponse;
import com.zxjk.duoduo.bean.response.GetRedPackageStatusResponse;
import com.zxjk.duoduo.bean.response.GetSerialBean;
import com.zxjk.duoduo.bean.response.GetSignListResponse;
import com.zxjk.duoduo.bean.response.GetSymbolInfo;
import com.zxjk.duoduo.bean.response.GetSymbolSerialResponse;
import com.zxjk.duoduo.bean.response.GetTransferAllResponse;
import com.zxjk.duoduo.bean.response.GetUInvitationUrlBean;
import com.zxjk.duoduo.bean.response.GetUpgradeGroupsResponnse;
import com.zxjk.duoduo.bean.response.GetVicinityResponse;
import com.zxjk.duoduo.bean.response.GetVideoInfoResponse;
import com.zxjk.duoduo.bean.response.GroupChatResponse;
import com.zxjk.duoduo.bean.response.GroupManagementInfoBean;
import com.zxjk.duoduo.bean.response.GroupResponse;
import com.zxjk.duoduo.bean.response.LoginResponse;
import com.zxjk.duoduo.bean.response.MarketsResponse;
import com.zxjk.duoduo.bean.response.PaymentDoneResponse;
import com.zxjk.duoduo.bean.response.PermissionInfoBean;
import com.zxjk.duoduo.bean.response.PersonalChatConfigResponse;
import com.zxjk.duoduo.bean.response.PersonalRedPackageInfoResponse;
import com.zxjk.duoduo.bean.response.ReceiveAirdropResponse;
import com.zxjk.duoduo.bean.response.ReceiveGroupRedPackageResponse;
import com.zxjk.duoduo.bean.response.ReceivePersonalRedPackageResponse;
import com.zxjk.duoduo.bean.response.ReceivePointResponse;
import com.zxjk.duoduo.bean.response.RedPackageResponse;
import com.zxjk.duoduo.bean.response.ReleaseRecord;
import com.zxjk.duoduo.bean.response.ReleaseRecordDetails;
import com.zxjk.duoduo.bean.response.RewardCodeResponse;
import com.zxjk.duoduo.bean.response.SearchCommunityResponse;
import com.zxjk.duoduo.bean.response.SendGroupRedPackageResponse;
import com.zxjk.duoduo.bean.response.ThirdPartyPaymentOrderResponse;
import com.zxjk.duoduo.bean.response.TransferResponse;
import com.zxjk.duoduo.bean.response.UpdateGroupInfoResponse;
import com.zxjk.duoduo.bean.response.WalletChainInfosResponse;
import com.zxjk.duoduo.bean.response.WechatChatRoomPermission;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    @GET
    Call<ResponseBody> downloadFile(@Url String url);

    @POST("duoduo/login")
    @FormUrlEncoded
    Observable<BaseResponse<LoginResponse>> login(
            @Field("mobile") String phone,
            @Field("pwd") String pwd
    );


    @POST("duoduo/getCode")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getCode(
            @Field("mobile") String phone,
            @Field("type") String type
    );

    @POST("duoduo/customer/updateCustomerInfo")
    @FormUrlEncoded
    Observable<BaseResponse<LoginResponse>> updateUserInfo(@Field("customerInfo") String customerInfo);

    @POST("duoduo/customer/updatePayPwd")
    @FormUrlEncoded
    Observable<BaseResponse<LoginResponse>> updatePayPwd(
            @Field("oldPayPwd") String oldPwd,
            @Field("newPayPwd") String newPwdOne,
            @Field("newPayPwdTwo") String newPwdTwo
    );

    @POST("duoduo/friend/getFriendInfoById")
    @FormUrlEncoded
    Observable<BaseResponse<FriendInfoResponse>> getFriendInfoById(@Field("friendId") String friendId);

    @POST("duoduo/friend/getFriendListById")
    Observable<BaseResponse<List<FriendInfoResponse>>> getFriendListById();

    @POST("duoduo/friend/searchCustomer")
    @FormUrlEncoded
    Observable<BaseResponse<List<FriendInfoResponse>>> searchCustomerInfo(@Field("data") String data, @Field("pageNoStr") String pageNoStr,
                                                                          @Field("pageSizeStr") String pageSizeStr);

    @POST("duoduo/friend/applyAddFriend")
    @FormUrlEncoded
    Observable<BaseResponse<String>> applyAddFriend(
            @Field("friendId") String friendId,
            @Field("remark") String remark,
            @Field("groupId") String groupId
    );

    @POST("duoduo/friend/getMyfriendsWaiting")
    Observable<BaseResponse<List<FriendInfoResponse>>> getMyfriendsWaiting();

    @FormUrlEncoded
    @POST("duoduo/friend/addFriend")
    Observable<BaseResponse<String>> addFriend(
            @Field("friendId") String friendId,
            @Field("markName") String markName
    );

    @POST("duoduo/customer/updateMobile")
    @FormUrlEncoded
    Observable<BaseResponse<String>> updateMobile(
            @Field("newMobile") String newMobile,
            @Field("securityCode") String securityCode
    );

    @POST("duoduo/group/getGroupByCustomId")
    @FormUrlEncoded
    Observable<BaseResponse<List<GroupChatResponse>>> getMygroupinformation(@Field("customerId") String customerId);


    @POST("duoduo/friend/deleteFriend")
    @FormUrlEncoded
    Observable<BaseResponse<String>> deleteFriend(@Field("friendId") String friendId);


    @POST("duoduo/loginOut")
    Observable<BaseResponse<String>> loginOut();


    @POST("duoduo/customer/certification")
    @FormUrlEncoded
    Observable<BaseResponse<String>> certification(@Field("data") String data);


    @POST("duoduo/friend/updateRemark")
    @FormUrlEncoded
    Observable<BaseResponse<FriendInfoResponse>> updateRemark(
            @Field("friendId") String friendId,
            @Field("remark") String remark
    );


    @POST("duoduo/customer/getCustomerAuth")
    Observable<BaseResponse<String>> getCustomerAuth();


    @POST("duoduo/customer/fandPayPwd")
    @FormUrlEncoded
    Observable<BaseResponse<String>> fandPayPwd(
            @Field("number") String number,
            @Field("securityCode") String securityCode,
            @Field("newPayPwd") String newPayPwd,
            @Field("newPayPwdTwo") String newPayPwdTwo

    );

    @POST("duoduo/group/makeGroup")
    @FormUrlEncoded
    Observable<BaseResponse<GroupResponse.GroupInfoBean>> makeGroup(
            @Field("groupOwnerId") String groupOwnerId,
            @Field("customerIds") String customerIds
    );

    @POST("duoduo/group/getGroupMemByGroupId")
    @FormUrlEncoded
    Observable<BaseResponse<List<AllGroupMembersResponse>>> getGroupMemByGroupId(@Field("groupId") String groupId);

    @POST("duoduo/group/enterGroup")
    @FormUrlEncoded
    Observable<BaseResponse<CommunityCultureResponse>> enterGroup(
            @Field("groupId") String groupId,
            @Field("inviterId") String inviterId,
            @Field("customerIds") String customerIds
    );

    @POST("duoduo/group/getGroupByGroupId")
    @FormUrlEncoded
    Observable<BaseResponse<GroupResponse>> getGroupByGroupId(@Field("groupId") String groupId);

    @POST("duoduo/group/getGroupByGroupId")
    @FormUrlEncoded
    Observable<BaseResponse<GroupResponse>> getGroupByGroupIdForQr(
            @Field("groupId") String groupId,
            @Field("type") String type
    );

    @POST("duoduo/group/updateGroupInfo")
    @FormUrlEncoded
    Observable<BaseResponse<UpdateGroupInfoResponse>> updateGroupInfo(@Field("groupInfo") String groupInfo);

    @POST("duoduo/group/disBandGroup")
    @FormUrlEncoded
    Observable<BaseResponse<String>> disBandGroup(
            @Field("groupId") String groupId,
            @Field("groupOwnerId") String groupOwnerId
    );

    @POST("duoduo/group/exitGroup")
    @FormUrlEncoded
    Observable<BaseResponse<String>> exitGroup(
            @Field("groupId") String groupId,
            @Field("customerId") String customerId
    );

    @POST("duoduo/group/moveOutGroup")
    @FormUrlEncoded
    Observable<BaseResponse<String>> moveOutGroup(
            @Field("groupId") String groupId,
            @Field("customerIds") String customerIds
    );

    @POST("duoduo/group/updateGroupOwner")
    @FormUrlEncoded
    Observable<BaseResponse<String>> updateGroupOwner(
            @Field("groupId") String groupId,
            @Field("customerId") String customerId
    );

    @POST("duoduo/wallet/ethTransaction")
    @FormUrlEncoded
    Observable<BaseResponse<String>> signTransaction(@Field("blockChainSerial") String blockChainSerial, @Field("password") String password);

    @POST("duoduo/redPackage/sendSingleRedPackage")
    @FormUrlEncoded
    Observable<BaseResponse<RedPackageResponse>> sendSingleRedPackage(@Field("data") String data);

    @POST("duoduo/customer/getCustomerInfoById")
    @FormUrlEncoded
    Observable<BaseResponse<LoginResponse>> getCustomerInfoById(@Field("id") String id);

    @POST("duoduo/customer/transfer")
    @FormUrlEncoded
    Observable<BaseResponse<TransferResponse>> transfer(@Field("toCustomerId") String toCustomerId,
                                                        @Field("hk") String hk, @Field("payPwd") String payPwd, @Field("remarks") String remarks, @Field("symbol") String symbol);

    @POST("duoduo/customer/collect")
    @FormUrlEncoded
    Observable<BaseResponse<TransferResponse>> collect(@Field("transferId") String transferId);

    @POST("duoduo/customer/getTransferInfo")
    @FormUrlEncoded
    Observable<BaseResponse<TransferResponse>> getTransferInfo(@Field("transferId") String transferId);


    @POST("duoduo/redPackage/receivePersonalRedPackage")
    @FormUrlEncoded
    Observable<BaseResponse<ReceivePersonalRedPackageResponse>> receivePersonalRedPackage(@Field("redPackageId") String redPackageId);

    @POST("duoduo/redPackage/getRedPackageStatus")
    @FormUrlEncoded
    Observable<BaseResponse<GetRedPackageStatusResponse>> getRedPackageStatus(@Field("redPackageId") String redPackageId
            , @Field("isGame") String isGame);

    @POST("duoduo/redPackage/personalRedPackageInfo")
    @FormUrlEncoded
    Observable<BaseResponse<PersonalRedPackageInfoResponse>> personalRedPackageInfo(@Field("redPackageId") String redPackageId,
                                                                                    @Field("customerId") int customerId);

    @POST("duoduo/redPackage/getGroupRedPackageInfo")
    @FormUrlEncoded
    Observable<BaseResponse<GetGroupRedPackageInfoResponse>> getGroupRedPackageInfo(@Field("redPackageId") String redPackageId);


    @POST("duoduo/customer/verifyPaperworkNumber")
    @FormUrlEncoded
    Observable<BaseResponse<String>> verifyPaperworkNumber(@Field("number") String number);


    @POST("duoduo/redPackage/sendGroupRedPackage")
    @FormUrlEncoded
    Observable<BaseResponse<SendGroupRedPackageResponse>> sendGroupRedPackage(@Field("data") String data);

    @POST("duoduo/redPackage/receiveGroupRedPackage")
    @FormUrlEncoded
    Observable<BaseResponse<ReceiveGroupRedPackageResponse>> receiveGroupRedPackage(@Field("redPackageId") String redPackageId
            , @Field("isGame") String isGame);

    @POST("duoduo/customer/getAppVersion")
    Observable<BaseResponse<GetAppVersionResponse>> getAppVersion();

    @POST("rest/160601/ocr/ocr_idcard.json")
    Observable<CardFaceBean> getOCRResult(@Body RequestBody body);

    @POST("rest/160601/ocr/ocr_idcard.json")
    Observable<CardBackBean> getOCRBackResult(@Body RequestBody body);

    @GET("mobileCheck")
    Observable<AuditCertificationBean> getCertification(@Query("idCard") String idCard, @Query("mobile") String mobile, @Query("name") String name);

    @POST("duoduo/customer/getVicinity")
    @FormUrlEncoded
    Observable<BaseResponse<List<GetVicinityResponse>>> getVicinity(@Field("lon") String lon, @Field("lat") String lat);

    @POST("duoduo/customer/operateOpenPhone")
    @FormUrlEncoded
    Observable<BaseResponse<String>> operateOpenPhone(@Field("openPhone") String openPhone);

    @POST("duoduo/customer/operateRealName")
    @FormUrlEncoded
    Observable<BaseResponse<String>> operateRealName(@Field("isShowRealname") String isShowRealname);

    @POST("duoduo/friend/getFriendsByMobiles")
    @FormUrlEncoded
    Observable<BaseResponse<List<GetFriendsByMobilesResponse>>> getFriendsByMobiles(@Field("mobiles") String mobiles, @Field("data") String data);

    @POST("duoduo/carouselMap/getCarouselMap")
    Observable<BaseResponse<List<GetCarouselMap>>> getCarouselMap();

    @POST("duoduo/blockchain/blockChainNews")
    @FormUrlEncoded
    Observable<BaseResponse<List<BlockChainNewsBean>>> blockChainNews(@Field("type") String type,
                                                                      @Field("pageNoStr") String pageNoStr, @Field("pageSizeStr") String pageSizeStr);

    @POST("mochart/quotes/getAllTickers")
    Observable<BaseResponse<List<MarketsResponse>>> markets();

    @POST("duoduo/chat/personalChatConfig")
    @FormUrlEncoded
    Observable<BaseResponse<PersonalChatConfigResponse>> personalChatConfig(@Field("targetId") String targetId);

    @POST("duoduo/chat/updateChatConfig")
    @FormUrlEncoded
    Observable<BaseResponse<String>> updateChatConfig(@Field("data") String data);

    @POST("duoduo/group/getPermissionInfo")
    @FormUrlEncoded
    Observable<BaseResponse<List<PermissionInfoBean>>> getPermissionInfo(@Field("groupId") String groupId);

    @POST("duoduo/group/updatePermissionInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> updatePermissionInfo(@Field("permissionInfo") String permissionInfo);

    @POST("duoduo/group/addPermissionInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> addPermissionInfo(@Field("groupId") String groupId, @Field("customerIds") String customerIds);

    @POST("duoduo/group/removePermissionInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> removePermissionInfo(@Field("groupId") String groupId, @Field("customerIds") String customerIds);

    @POST("duoduo/group/muteGroups")
    @FormUrlEncoded
    Observable<BaseResponse<String>> muteGroups(@Field("groupId") String groupId, @Field("type") String type);

    @POST("duoduo/group/groupOperation")
    @FormUrlEncoded
    Observable<BaseResponse<String>> groupOperation(@Field("groupId") String groupId, @Field("type") String type, @Field("source") String source);

    @POST("duoduo/customer/getSignList")
    Observable<BaseResponse<GetSignListResponse>> getSignList();

    @POST("duoduo/customer/createSign")
    Observable<BaseResponse<GetSignListResponse>> createSign();

    @POST("duoduo/customer/receivePoint")
    @FormUrlEncoded
    Observable<BaseResponse<ReceivePointResponse>> receivePoint(@Field("type") String type);

    @POST("duoduo/customer/savePointInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> savePointInfo(@Field("type") String type);

    @POST("duoduo/group/getGroupPayInfo")
    @FormUrlEncoded
    Observable<BaseResponse<GetGroupPayInfoResponse>> getGroupPayInfo(@Field("groupId") String groupId);

    @POST("duoduo/group/groupPayInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> groupPayInfo(@Field("groupId") String groupId, @Field("payFee") String payFee, @Field("isOpen") String isOpen, @Field("symbol") String symbol);

    @POST("duoduo/group/payToGroup")
    @FormUrlEncoded
    Observable<BaseResponse<CommunityCultureResponse>> payToGroup(@Field("groupId") String groupId, @Field("toCustomerId") String toCustomerId, @Field("payPwd") String payPwd, @Field("mot") String mot,
                                                                  @Field("symbol") String symbol);

    @POST("duoduo/group/getRedNewPersonInfo")
    @FormUrlEncoded
    Observable<BaseResponse<GetRedNewPersonInfoResponse>> getRedNewPersonInfo(@Field("groupId") String groupId);

    @POST("duoduo/group/upRedNewPersonInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> upRedNewPersonInfo(@Field("data") String data);

    @POST("duoduo/redPackage/receiveNewPersonRedPackage")
    @FormUrlEncoded
    Observable<BaseResponse<GetRedNewPersonInfoResponse>> receiveNewPersonRedPackage(@Field("groupId") String groupId);

    @POST("duoduo/customer/getInviteInfo")
    @FormUrlEncoded
    Observable<BaseResponse<GetInviteInfoResponse>> getInviteInfo(@Field("page") int page);

    @POST("duoduo/customer/appUserRegisterAndLogin")
    @FormUrlEncoded
    Observable<BaseResponse<LoginResponse>> appUserRegisterAndLogin(@Field("mobile") String mobile,
                                                                    @Field("securityCode") String securityCode,
                                                                    @Field("inviteId") String inviteId,
                                                                    @Field("communityId") String communityId);

    @POST("duoduo/group/getUpgradeGroups")
    @FormUrlEncoded
    Observable<BaseResponse<GetUpgradeGroupsResponnse>> getUpgradeGroups(@Field("groupId") String groupId);

    @POST("duoduo/group/payToUpgradeGroup")
    @FormUrlEncoded
    Observable<BaseResponse<String>> payToUpgradeGroup(@Field("groupId") String groupId,
                                                       @Field("payPwd") String payPwd,
                                                       @Field("groupTag") String groupTag,
                                                       @Field("mot") String mot);

    @POST("duoduo/rongcloud/recallGroupMessage")
    @FormUrlEncoded
    Observable<BaseResponse<String>> recallGroupMessage(@Field("uId") String uId);

    @POST("duoduo/wallet/getWalletChainInfos")
    Observable<BaseResponse<WalletChainInfosResponse>> getWalletChainInfos();

    @POST("duoduo/currency/currencyInfosByCustomerId")
    @FormUrlEncoded
    Observable<BaseResponse<List<CurrencyInfosByCustomerBean>>> currencyInfosByCustomerId(@Field("coinType") String coinType);

    @POST("duoduo/wallet/generateMnemonic")
    @FormUrlEncoded
    Observable<BaseResponse<GenerateMnemonicResponse>> generateMnemonic(@Field("symbol") String symbol, @Field("pwd") String pwd);

    @POST("duoduo/wallet/getMainSymbolByCustomerId")
    Observable<BaseResponse<List<GetMainSymbolByCustomerIdBean>>> getMainSymbolByCustomerId();

    @POST("duoduo/wallet/checkWalletPwd")
    @FormUrlEncoded
    Observable<BaseResponse<String>> checkWalletPwd(@Field("pwd") String pwd);

    @POST("duoduo/wallet/getBalanceByAddress")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getBalanceByAddress(@Field("symbol") String symbol, @Field("walletAddress") String walletAddress);

    @POST("duoduo/wallet/updateWalletChainName")
    @FormUrlEncoded
    Observable<BaseResponse<String>> updateWalletChainName(@Field("walletName") String walletName, @Field("walletAddress") String walletAddress);

    @POST("duoduo/wallet/exportWalletInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> exportWalletInfo(@Field("walletAddress") String walletAddress, @Field("pwd") String pwd, @Field("type") String type);

    @POST("duoduo/wallet/deleteWallet")
    @FormUrlEncoded
    Observable<BaseResponse<String>> deleteWalletByKey(@Field("walletAddress") String walletAddress, @Field("importMethod") String importMethod, @Field("symbol") String symbol
            , @Field("pwd") String pwd);

    @POST("duoduo/wallet/deleteWallet")
    @FormUrlEncoded
    Observable<BaseResponse<String>> deleteWalletByWords(@Field("walletAddress") String walletAddress, @Field("importMethod") String importMethod, @Field("symbol") String symbol
            , @Field("walletMnemonic") String walletMnemonic);

    @POST("duoduo/wallet/importPrivateKey")
    @FormUrlEncoded
    Observable<BaseResponse<String>> importPrivateKey(@Field("symbol") String symbol, @Field("privateKey") String privateKey, @Field("pwd") String pwd);

    @POST("duoduo/wallet/importByMnemonic")
    @FormUrlEncoded
    Observable<BaseResponse<String>> importByMnemonic(@Field("symbol") String symbol, @Field("mnemonicListStr") String mnemonicListStr, @Field("pwd") String pwd);

    @POST("duoduo/wallet/importKeyStore")
    @FormUrlEncoded
    Observable<BaseResponse<String>> importKeyStore(@Field("symbol") String symbol, @Field("keystore") String keystore, @Field("pwd") String pwd);

    @POST("duoduo/wallet/getTransferAll")
    @FormUrlEncoded
    Observable<BaseResponse<GetTransferAllResponse>> getTransferAll(@Field("address") String address, @Field("page") String page, @Field("offset") String offset, @Field("symbol") String symbol);

    @POST("duoduo/wallet/assetManage")
    Observable<BaseResponse<List<AssetManageBean>>> assetManage();

    @POST("duoduo/wallet/operateAssets")
    @FormUrlEncoded
    Observable<BaseResponse<String>> operateAssets(@Field("walletChainInfo") String walletChainInfo);

    @POST("duoduo/wallet/isExistWalletInfo")
    Observable<BaseResponse<String>> isExistWalletInfo();

    @POST("duoduo/walletBalance/getSerial")
    @FormUrlEncoded
    Observable<BaseResponse<List<GetSerialBean>>> getSerial(@Field("pageSizeStr") String pageSizeStr,
                                                            @Field("pageNoStr") String pageNoStr, @Field("platform") String platform);

    @POST("duoduo/walletBalance/getBalanceInfo")
    Observable<BaseResponse<GetBalanceInfoResponse>> getBalanceInfo();

    @POST("duoduo/walletBalance/getPaymentList")
    Observable<BaseResponse<List<GetPaymentListBean>>> getPaymentList();

    @POST("duoduo/walletBalance/getSymbolSerial")
    @FormUrlEncoded
    Observable<BaseResponse<GetSymbolSerialResponse>> getSymbolSerial(@Field("pageSizeStr") String pageSizeStr,
                                                                      @Field("pageNoStr") String pageNoStr, @Field("symbol") String symbol, @Field("parentSymbol") String parentSymbol);

    @POST("duoduo/currency/getParentSymbol")
    @FormUrlEncoded
    Observable<BaseResponse<List<GetParentSymbolBean>>> getParentSymbol(@Field("symbol") String symbol);

    @POST("duoduo/wallet/getBalanceInfoByAddress")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getBalanceInfoByAddress(@Field("walletAddress") String walletAddress, @Field("coinType") String coinType, @Field("parentSymbol") String parentSymbol, @Field("contractAddress") String contractAddress, @Field("tokenDecimal") String tokenDecimal);

    @POST("duoduo/community/communityList")
    Observable<BaseResponse<List<CommunityListBean>>> communityList();

    @POST("duoduo/community/saveCommuntiy")
    @FormUrlEncoded
    Observable<BaseResponse<CommunityInfoResponse>> saveCommuntiy(@Field("data") String data);

    @POST("duoduo/community/communityInfo")
    @FormUrlEncoded
    Observable<BaseResponse<CommunityInfoResponse>> communityInfo(@Field("groupId") String groupId);

    @POST("duoduo/customer/getAuthToken")
    Observable<BaseResponse<String>> getAuthToken();

    @POST("duoduo/community/searchCommunity")
    @FormUrlEncoded
    Observable<BaseResponse<SearchCommunityResponse>> searchCommunity(@Field("code") String code
            , @Field("page") int page, @Field("offset") int offset);

    @POST("duoduo/community/communityCulture")
    @FormUrlEncoded
    Observable<BaseResponse<CommunityCultureResponse>> communityCulture(@Field("groupId") String id);

    @POST("duoduo/community/editCommunity")
    @FormUrlEncoded
    Observable<BaseResponse<EditCommunityResponse>> editCommunity(@Field("data") String data);

    @POST("duoduo/community/editListCommunityCulture")
    @FormUrlEncoded
    Observable<BaseResponse<EditListCommunityCultureResponse>> editListCommunityCulture(@Field("groupId") String groupId);

    @POST("duoduo/community/editCommunityWebSite")
    @FormUrlEncoded
    Observable<BaseResponse<String>> editCommunityWebSite(@Field("data") String data);

    @POST("duoduo/customer/getUInvitationUrl")
    @FormUrlEncoded
    Observable<BaseResponse<GetUInvitationUrlBean>> getUInvitationUrl(@Field("communityId") String communityId);

    @POST("duoduo/customer/getUInvitationUrl")
    Observable<BaseResponse<GetUInvitationUrlBean>> getUInvitationUrl();

    @POST("duoduo/customer/initAuthData")
    Observable<BaseResponse<String>> initAuthData();

    @POST("duoduo/community/editCommunityVideo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> editCommunityVideo(@Field("data") String data);

    @POST("duoduo/community/communityVideoList")
    @FormUrlEncoded
    Observable<BaseResponse<CommunityVideoListResponse>> communityVideoList(@Field("groupId") String groupId);

    @POST("duoduo/community/communityApplicationList")
    @FormUrlEncoded
    Observable<BaseResponse<CommunityApplicationListResponse>> communityApplicationList(@Field("groupId") String groupId);

    @POST("duoduo/community/editCommunityApplication")
    @FormUrlEncoded
    Observable<BaseResponse<String>> editCommunityApplication(@Field("data") String data);

    @POST("duoduo/community/editCommunityFile")
    @FormUrlEncoded
    Observable<BaseResponse<String>> editCommunityFile(@Field("data") String data);

    @POST("duoduo/community/communityFilesList")
    @FormUrlEncoded
    Observable<BaseResponse<CommunityFilesListResponse>> communityFilesList(@Field("groupId") String id);

    @POST("duoduo/customer/getCustomerBasicInfoById")
    @FormUrlEncoded
    Observable<BaseResponse<GetCustomerBasicInfoByIdResponse>> getCustomerBasicInfoById(@Field("friendId") String id);

    @POST("duoduo/group/getGroupChatInfoByGroupId")
    @FormUrlEncoded
    Observable<BaseResponse<GetGroupChatInfoByGroupIdResponse>> getGroupChatInfoByGroupId(@Field("groupId") String id);

    @POST("mochat/shopping/getShoppingUrl")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getShoppingUrl(@Field("type") String type);


    @POST("duoduo/customer/thirdPartLogin")
    @FormUrlEncoded
    Observable<BaseResponse<String>> htmlLogin(@Field("appId") String appid, @Field("randomStr") String randomStr, @Field("sign") String sign);

    @POST("duoduo/customer/getAppVersionBysystemType")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getAppVersionBysystemType(@Field("systemType") String systemType);

    @POST("duoduo/airdrop/airdropInfo")
    Observable<BaseResponse<AirdropInfoResponse>> airdropInfo();

    @POST("duoduo/airdrop/receiveAirdrop")
    @FormUrlEncoded
    Observable<BaseResponse<ReceiveAirdropResponse>> receiveAirdrop(@Field("click") String click);

    @POST("duoduo/airdrop/shareAirdrop")
    Observable<BaseResponse<String>> shareAirdrop();

    @FormUrlEncoded
    @POST("duoduo/group/getReleaseRecord")
    Observable<BaseResponse<List<ReleaseRecord>>> releaseRecord(@Field("groupId") String groupId);

    @FormUrlEncoded
    @POST("duoduo/group/getReleaseRecordDetails")
    Observable<BaseResponse<ReleaseRecordDetails>> releaseRecordDetails(@Field("groupId") String groupId, @Field("symbol") String symbol, @Field("airdropId") String airdropId, @Field("page") String page, @Field("offset") String offset);

    @FormUrlEncoded
    @POST("duoduo/walletBalance/balanceManage")
    Observable<BaseResponse<String>> balanceManage(@Field("currencyName") String currencyName, @Field("isClose") String isClose);

    @POST("duoduo/walletBalance/balanceAssetManage")
    Observable<BaseResponse<List<BalanceAssetManageBean>>> balanceAssetManage();

    @FormUrlEncoded
    @POST("duoduo/group/getMemberIdByGroupId")
    Observable<BaseResponse<ArrayList<String>>> getMemberIdByGroupId(@Field("groupId") String groupId);

    @POST("duoduo/purchase/getSymbolInfo")
    Observable<BaseResponse<GetSymbolInfo>> getSymbolInfo();

    @FormUrlEncoded
    @POST("otc/active/find/hailang")
    Observable<BaseResponse<ArrayList<FindHailangResponse>>> findhailang(@Field("currency") String currency, @Field("nonce") String nonce);

    @FormUrlEncoded
    @POST("duoduo/purchase/bycoins")
    Observable<BaseResponse<ByBoinsResponse>> byCoins(@Field("active_id") String activeId,
                                                      @Field("by_cny") String byCny,
                                                      @Field("currency") String currency,
                                                      @Field("nonce") String nonce,
                                                      @Field("price") String price,
                                                      @Field("user_id") String userId,
                                                      @Field("payment_type") String paymentType);

    @FormUrlEncoded
    @POST("duoduo/purchase/byAmount")
    Observable<BaseResponse<ByBoinsResponse>> byAmount(@Field("user_id") String userId,
                                                       @Field("currency") String currency,
                                                       @Field("by_amount") String byAmount,
                                                       @Field("active_id") String activeId,
                                                       @Field("nonce") String nonce,
                                                       @Field("payment_type") String paymentType);

    @FormUrlEncoded
    @POST("duoduo/purchase/removeOrder")
    Observable<BaseResponse<String>> removeOrder(@Field("trans_id") String transId,
                                                 @Field("type") String type,
                                                 @Field("nonce") String nonce,
                                                 @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("duoduo/purchase/paymentDone")
    Observable<BaseResponse<PaymentDoneResponse>> paymentDone(@Field("collection_id") String collectionId,
                                                              @Field("nonce") String nonce,
                                                              @Field("trans_id") String transId,
                                                              @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("duoduo/purchase/getOrderInfoByType")
    Observable<BaseResponse<GetOrderInfoByTypeResponse>> getOrderInfoByType(@Field("page") String page,
                                                                            @Field("offset") String offset,
                                                                            @Field("side") String side,
                                                                            @Field("state") String state);

    @FormUrlEncoded
    @POST("duoduo/live/createLive")
    Observable<BaseResponse<String>> createLive(@Field("chatRoom") String chatRoom, @Field("liveType") String liveType);

    @FormUrlEncoded
    @POST("duoduo/chatRoom/getChatRoomInfo")
    Observable<BaseResponse<GetChatRoomInfoResponse>> getChatRoomInfo(@Field("roomId") String roomId);

    @FormUrlEncoded
    @POST("duoduo/live/enableOpenLive")
    Observable<BaseResponse<String>> enableOpenLive(@Field("groupId") String groupId);

    @GET("duoduo/live/shareToWx")
    Observable<BaseResponse<String>> shareToWx();

    @FormUrlEncoded
    @POST("duoduo/live/delLive")
    Observable<BaseResponse<String>> delLive(@Field("roomId") String roomId);

    @POST("duoduo/live/toLiveList")
    Observable<BaseResponse<List<CastListBean>>> toLiveList();

    @FormUrlEncoded
    @POST("duoduo/live/getGroupLiveGoingInfo")
    Observable<BaseResponse<List<GetChatRoomInfoResponse>>> getGroupLiveGoingInfo(@Field("groupId") String groupId);

    @FormUrlEncoded
    @POST("duoduo/purchase/orderInfo")
    Observable<BaseResponse<ByBoinsResponse>> orderInfo(@Field("nonce") String nonce,
                                                        @Field("trans_id") String trans_id,
                                                        @Field("user_id") String user_id,
                                                        @Field("paymentType") String paymentType,
                                                        @Field("createTime") String createTime);

    @FormUrlEncoded
    @POST("duoduo/purchase/orderAppeal")
    Observable<BaseResponse<String>> orderAppeal(@Field("img") String img,
                                                 @Field("nonce") String nonce,
                                                 @Field("phone") String phone,
                                                 @Field("reason") String reason,
                                                 @Field("trans_id") String trans_id,
                                                 @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("duoduo/live/getRoomPermissionByRoomId")
    Observable<BaseResponse<WechatChatRoomPermission>> getRoomPermissionByRoomId(@Field("roomId") String roomId);

    @FormUrlEncoded
    @POST("duoduo/live/modifyLive")
    Observable<BaseResponse<String>> modifyLive(@Field("chatRoom") String chatRoom, @Field("liveType") String liveType);

    @FormUrlEncoded
    @POST("duoduo/live/endLive")
    Observable<BaseResponse<String>> endLive(@Field("roomId") String roomId);

    @FormUrlEncoded
    @POST("duoduo/live/updateRoomPermissionByRoomId")
    Observable<BaseResponse<String>> updateRoomPermissionByRoomId(@Field("roomId") String roomId, @Field("permission") String permission, @Field("status") String status);

    @FormUrlEncoded
    @POST("duoduo/live/getRoomStatusByRoomId")
    Observable<BaseResponse<String>> getRoomStatusByRoomId(@Field("roomId") String roomId);

    @FormUrlEncoded
    @POST("duoduo/group/getDumbManagers")
    Observable<BaseResponse<ArrayList<GroupManagementInfoBean>>> getDumbManagers(@Field("groupId") String groupId,
                                                                                 @Field("page") String page,
                                                                                 @Field("offset") String offset,
                                                                                 @Field("searchKey") String searchKey);

    @POST("duoduo/group/memberMutedOperation")
    @FormUrlEncoded
    Observable<BaseResponse<String>> muteMembers(@Field("groupId") String groupId,
                                                 @Field("customerId") String customerId,
                                                 @Field("type") String type);

    @POST("duoduo/group/blacklistOperation")
    @FormUrlEncoded
    Observable<BaseResponse<String>> blacklistOperation(@Field("groupId") String groupId,
                                                        @Field("customerId") String customerId,
                                                        @Field("type") String type);

    @POST("duoduo/live/getOnlineUsers")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getOnlineUsers(@Field("roomId") String roomId);

    @POST("duoduo/group/recallAndMuted")
    @FormUrlEncoded
    Observable<BaseResponse<String>> recallAndMuted(@Field("customerId") String customerId, @Field("groupId") String groupId);

    @POST("duoduo/purchase/getOpenPurchaseStatus")
    Observable<BaseResponse<String>> getOpenPurchaseStatus();

    @POST("duoduo/customer/feedback")
    @FormUrlEncoded
    Observable<BaseResponse<String>> feedback(@Field("content") String content);

    @POST("duoduo/live/getVideoInfo")
    @FormUrlEncoded
    Observable<BaseResponse<GetVideoInfoResponse>> getVideoInfo(@Field("roomId") String roomId);

    @POST("duoduo/live/getLiveInfoByGroupId")
    @FormUrlEncoded
    Observable<BaseResponse<ArrayList<GetLiveInfoByGroupIdResponse>>> getLiveInfoByGroupId(@Field("groupId") String groupId);

    @POST("duoduo/customer/getThirdPartyPaymentOrder")
    @FormUrlEncoded
    Observable<BaseResponse<ThirdPartyPaymentOrderResponse>> getThirdPartyPaymentOrder(@Field("orderId") String orderId);

    @POST("duoduo/customer/thirdPartyPayment")
    @FormUrlEncoded
    Observable<BaseResponse<String>> thirdPartyPayment(@Field("orderId") String orderId, @Field("payPwd") String payPwd);

    @POST("duoduo/redPackage/getRewardCode")
    @FormUrlEncoded
    Observable<BaseResponse<RewardCodeResponse>> getRewardCode(@Field("code") String code);


    @POST("duoduo/live/enableOpenVideoLive")
    Observable<BaseResponse<String>> enableOpenVideoLive();

    @POST("duoduo/community/recommendCommunity")
    @FormUrlEncoded
    Observable<BaseResponse<ArrayList<GetRecommendCommunity>>> recommendCommunity(@Field("currPage") String currPage);

    @POST("duoduo/order/addSubmitOrder")
    @FormUrlEncoded
    Observable<BaseResponse<String>> addSubmitOrder(@Field("payPwd") String payPwd,
                                                    @Field("qrcode") String qrcode,
                                                    @Field("payAmount") String payAmount,
                                                    @Field("currency") String currency);

    @POST("duoduo/order/getSymbolPrice")
    Observable<BaseResponse<ArrayList<GetPaymentListBean>>> getSymbolPrice();
}