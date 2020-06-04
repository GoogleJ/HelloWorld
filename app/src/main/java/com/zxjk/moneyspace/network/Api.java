package com.zxjk.moneyspace.network;

import com.zxjk.moneyspace.bean.AuditCertificationBean;
import com.zxjk.moneyspace.bean.CardBackBean;
import com.zxjk.moneyspace.bean.CardFaceBean;
import com.zxjk.moneyspace.bean.response.AllGroupMembersResponse;
import com.zxjk.moneyspace.bean.response.AssetManageBean;
import com.zxjk.moneyspace.bean.response.BalanceAssetManageBean;
import com.zxjk.moneyspace.bean.response.BaseResponse;
import com.zxjk.moneyspace.bean.response.BlockChainNewsBean;
import com.zxjk.moneyspace.bean.response.ByBoinsResponse;
import com.zxjk.moneyspace.bean.response.CnyRechargeResponse;
import com.zxjk.moneyspace.bean.response.CommunityCultureResponse;
import com.zxjk.moneyspace.bean.response.CurrencyInfosByCustomerBean;
import com.zxjk.moneyspace.bean.response.EditCommunityResponse;
import com.zxjk.moneyspace.bean.response.FindHailangResponse;
import com.zxjk.moneyspace.bean.response.FriendInfoResponse;
import com.zxjk.moneyspace.bean.response.GenerateMnemonicResponse;
import com.zxjk.moneyspace.bean.response.GetAppVersionResponse;
import com.zxjk.moneyspace.bean.response.GetBalanceInfoResponse;
import com.zxjk.moneyspace.bean.response.GetBuyList;
import com.zxjk.moneyspace.bean.response.GetCarouselMap;
import com.zxjk.moneyspace.bean.response.GetChatRoomInfoResponse;
import com.zxjk.moneyspace.bean.response.GetCustomerBankInfoResponse;
import com.zxjk.moneyspace.bean.response.GetCustomerBasicInfoByIdResponse;
import com.zxjk.moneyspace.bean.response.GetCustomerIdentity;
import com.zxjk.moneyspace.bean.response.GetEcologyByType;
import com.zxjk.moneyspace.bean.response.GetFriendsByMobilesResponse;
import com.zxjk.moneyspace.bean.response.GetGroupChatInfoByGroupIdResponse;
import com.zxjk.moneyspace.bean.response.GetGroupRedPackageInfoResponse;
import com.zxjk.moneyspace.bean.response.GetMainSymbolByCustomerIdBean;
import com.zxjk.moneyspace.bean.response.GetOTCSymbolInfo;
import com.zxjk.moneyspace.bean.response.GetOrderInfoById;
import com.zxjk.moneyspace.bean.response.GetOrderInfoByTypeResponse;
import com.zxjk.moneyspace.bean.response.GetParentSymbolBean;
import com.zxjk.moneyspace.bean.response.GetPaymentListBean;
import com.zxjk.moneyspace.bean.response.GetPublicGroupResponse;
import com.zxjk.moneyspace.bean.response.GetRedNewPersonInfoResponse;
import com.zxjk.moneyspace.bean.response.GetRedPackageRecordResponse;
import com.zxjk.moneyspace.bean.response.GetRedPackageStatusResponse;
import com.zxjk.moneyspace.bean.response.GetSerialBean;
import com.zxjk.moneyspace.bean.response.GetSymbolSerialResponse;
import com.zxjk.moneyspace.bean.response.GetTransferAllResponse;
import com.zxjk.moneyspace.bean.response.GetUpgradeGroupsResponnse;
import com.zxjk.moneyspace.bean.response.GetVicinityResponse;
import com.zxjk.moneyspace.bean.response.GroupChatResponse;
import com.zxjk.moneyspace.bean.response.GroupResponse;
import com.zxjk.moneyspace.bean.response.LoginResponse;
import com.zxjk.moneyspace.bean.response.MarketsResponse;
import com.zxjk.moneyspace.bean.response.PayInfoResponse;
import com.zxjk.moneyspace.bean.response.PaymentDoneResponse;
import com.zxjk.moneyspace.bean.response.PersonalChatConfigResponse;
import com.zxjk.moneyspace.bean.response.PersonalRedPackageInfoResponse;
import com.zxjk.moneyspace.bean.response.ReceiveGroupRedPackageResponse;
import com.zxjk.moneyspace.bean.response.ReceivePersonalRedPackageResponse;
import com.zxjk.moneyspace.bean.response.RedPackageResponse;
import com.zxjk.moneyspace.bean.response.SearchCommunityResponse;
import com.zxjk.moneyspace.bean.response.SendGroupRedPackageResponse;
import com.zxjk.moneyspace.bean.response.TransferResponse;
import com.zxjk.moneyspace.bean.response.UpdateGroupInfoResponse;
import com.zxjk.moneyspace.bean.response.UserSellResponse;
import com.zxjk.moneyspace.bean.response.WalletChainInfosResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {

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
    Observable<BaseResponse<FriendInfoResponse>> getFriendInfoById(@Field("friendId") String friendId,@Field("groupId") String groupId);

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

    @POST("duoduo/customer/getPayInfo")
    Observable<BaseResponse<List<PayInfoResponse>>> getPayInfo();

    @POST("duoduo/friend/deleteFriend")
    @FormUrlEncoded
    Observable<BaseResponse<String>> deleteFriend(@Field("friendId") String friendId);

    @POST("duoduo/customer/updatePwd")
    @FormUrlEncoded
    Observable<BaseResponse<String>> updatePwd(
            @Field("oldPwd") String oldPwd,
            @Field("newPwdOne") String newPwdOne,
            @Field("newPwdTwo") String newPwdTwo
    );

    @POST("duoduo/loginOut")
    Observable<BaseResponse<String>> loginOut();

    @POST("duoduo/customer/addPayInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> addPayInfo(@Field("data") String data);

    @POST("duoduo/customer/certification")
    @FormUrlEncoded
    Observable<BaseResponse<String>> certification(@Field("data") String data);

    @POST("duoduo/exchange/closeSellOrder")
    @FormUrlEncoded
    Observable<BaseResponse<String>> closeSellOrder(@Field("sellOrderId") String sellOrderId);

    @POST("duoduo/exchange/cancelled")
    @FormUrlEncoded
    Observable<BaseResponse<String>> cancelled(@Field("buyOrderId") String buyOrderId
            , @Field("bothOrderId") String bothOrderId
            , @Field("sellOrderId") String sellOrderId);

    @POST("duoduo/exchange/isConfine")
    Observable<BaseResponse<String>> isConfine();

    @POST("duoduo/exchange/overOrder")
    @FormUrlEncoded
    Observable<BaseResponse<String>> overOrder(@Field("buyCustomerId") String buyCustomerId
            , @Field("buyOrderId") String buyOrderId
            , @Field("sellOrderId") String sellOrderId
            , @Field("bothOrderId") String bothOrderId
            , @Field("payPwd") String payPwd);

    @POST("duoduo/exchange/rejectAudit")
    @FormUrlEncoded
    Observable<BaseResponse<String>> rejectAudit(
            @Field("buyOrderId") String buyOrderId
            , @Field("bothOrderId") String bothOrderId
            , @Field("sellOrderId") String sellOrderId);

    @POST("duoduo/friend/updateRemark")
    @FormUrlEncoded
    Observable<BaseResponse<FriendInfoResponse>> updateRemark(
            @Field("friendId") String friendId,
            @Field("remark") String remark
    );

    @POST("duoduo/exchange/updateBuyPayState")
    @FormUrlEncoded
    Observable<BaseResponse<String>> updateBuyPayState(@Field("bothOrderId") String bothOrderId
            , @Field("picture") String picture);

    @POST("duoduo/customer/updatePayInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> updatePayInfo(@Field("payType") String payType);

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

    @POST("duoduo/redPackage/getRedPackageRecord")
    @FormUrlEncoded
    Observable<BaseResponse<GetRedPackageRecordResponse>> getRedPackageRecord(@Field("type") String type);

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

    @POST("duoduo/customer/verifyPayPwd")
    @FormUrlEncoded
    Observable<BaseResponse<String>> verifyPayPwd(@Field("payPwd") String payPwd);

    @POST("duoduo/customer/verifyPaperworkNumber")
    @FormUrlEncoded
    Observable<BaseResponse<String>> verifyPaperworkNumber(@Field("number") String number);

    @POST("duoduo/exchange/addAppeal")
    @FormUrlEncoded
    Observable<BaseResponse<String>> addAppeal(@Field("orderAppealStr") String orderAppealStr);

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

    @POST("duoduo/group/addPermissionInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> addPermissionInfo(@Field("groupId") String groupId, @Field("customerIds") String customerIds);

    @POST("duoduo/group/removePermissionInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> removePermissionInfo(@Field("groupId") String groupId, @Field("customerIds") String customerIds);

    @POST("duoduo/customer/savePointInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> savePointInfo(@Field("type") String type);

    @POST("duoduo/redPackage/receiveNewPersonRedPackage")
    @FormUrlEncoded
    Observable<BaseResponse<GetRedNewPersonInfoResponse>> receiveNewPersonRedPackage(@Field("groupId") String groupId);

    @POST("duoduo/customer/appUserRegisterAndLogin")
    @FormUrlEncoded
    Observable<BaseResponse<LoginResponse>> appUserRegisterAndLogin(@Field("mobile") String mobile, @Field("securityCode") String securityCode);

    @POST("duoduo/customer/appUserRegisterAndLogin")
    @FormUrlEncoded
    Observable<BaseResponse<LoginResponse>> appUserRegisterAndLoginEmail(@Field("email") String email, @Field("securityCode") String securityCode);

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

    @POST("duoduo/customer/getAuthToken")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getAuthToken(@Field("info") String info, @Field("name") String name, @Field("card") String card);

    @POST("duoduo/community/searchCommunity")
    @FormUrlEncoded
    Observable<BaseResponse<SearchCommunityResponse>> searchCommunity(@Field("code") String code
            , @Field("page") int page, @Field("offset") int offset);

    @POST("duoduo/community/editCommunity")
    @FormUrlEncoded
    Observable<BaseResponse<EditCommunityResponse>> editCommunity(@Field("data") String data);

    @POST("duoduo/customer/initAuthData")
    Observable<BaseResponse<String>> initAuthData();

    @POST("duoduo/customer/getCustomerBasicInfoById")
    @FormUrlEncoded
    Observable<BaseResponse<GetCustomerBasicInfoByIdResponse>> getCustomerBasicInfoById(@Field("friendId") String id);

    @POST("duoduo/group/getGroupChatInfoByGroupId")
    @FormUrlEncoded
    Observable<BaseResponse<GetGroupChatInfoByGroupIdResponse>> getGroupChatInfoByGroupId(@Field("groupId") String id);

    @POST("mochat/shopping/getShoppingUrl")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getShoppingUrl(@Field("type") String type);

    @POST("duoduo/customer/authorizedLogin")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getAuthorization(@Field("appId") String appid);

    @POST("duoduo/customer/thirdPartLogin")
    @FormUrlEncoded
    Observable<BaseResponse<String>> htmlLogin(@Field("appId") String appid, @Field("randomStr") String randomStr, @Field("sign") String sign);

    @POST("duoduo/customer/getAppVersionBysystemType")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getAppVersionBysystemType(@Field("systemType") String systemType);

    @POST("duoduo/airdrop/shareAirdrop")
    Observable<BaseResponse<String>> shareAirdrop();

    @FormUrlEncoded
    @POST("duoduo/walletBalance/balanceManage")
    Observable<BaseResponse<String>> balanceManage(@Field("currencyName") String currencyName, @Field("isClose") String isClose);

    @POST("duoduo/walletBalance/balanceAssetManage")
    Observable<BaseResponse<List<BalanceAssetManageBean>>> balanceAssetManage();

    @FormUrlEncoded
    @POST("duoduo/exchange/getOTCSymbolInfo")
    Observable<BaseResponse<GetOTCSymbolInfo>> getOTCSymbolInfo(@Field("nonce") String nonce);

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
    @POST("duoduo/exchange/getOrderList")
    Observable<BaseResponse<List<GetOrderInfoByTypeResponse>>> getOrderInfoByType(@Field("nonce") String nonce,
                                                                                  @Field("status") String status,
                                                                                  @Field("buyOrSell") String buyOrSell);

    @FormUrlEncoded
    @POST("duoduo/live/getGroupLiveGoingInfo")
    Observable<BaseResponse<List<GetChatRoomInfoResponse>>> getGroupLiveGoingInfo(@Field("groupId") String groupId);

    @FormUrlEncoded
    @POST("duoduo/exchange/getOrderInfoById")
    Observable<BaseResponse<GetOrderInfoById>> getOrderInfoById(@Field("nonce") String nonce,
                                                                @Field("bothOrderId") String bothOrderId,
                                                                @Field("buyOrderId") String buyOrderId,
                                                                @Field("sellOrderId") String sellOrderId,
                                                                @Field("buyOrSell") String buyOrSell);

    @FormUrlEncoded
    @POST("duoduo/exchange/addAppeal")
    Observable<BaseResponse<String>> orderAppeal(@Field("appealReason") String appealReason,
                                                 @Field("bothOrderId") String bothOrderId,
                                                 @Field("nonce") String nonce,
                                                 @Field("picture") String picture,
                                                 @Field("plaintiffId") String plaintiffId);

    @POST("duoduo/live/getOnlineUsers")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getOnlineUsers(@Field("roomId") String roomId);

    @POST("duoduo/group/recallAndMuted")
    @FormUrlEncoded
    Observable<BaseResponse<String>> recallAndMuted(@Field("customerId") String customerId, @Field("groupId") String groupId);

    @POST("duoduo/purchase/getOpenPurchaseStatus")
    Observable<BaseResponse<String>> getOpenPurchaseStatus();

    @POST("duoduo/group/getPublicGroupList")
    Observable<BaseResponse<List<GetPublicGroupResponse>>> getPublicGroupList(@Field("groupName") String groupName);

    @POST("duoduo/mail/getEmailCode")
    @FormUrlEncoded
    Observable<BaseResponse<String>> getEmailCode(@Field("email") String email);

    @POST("duoduo/customer/getRedOperatorIdentity")
    Observable<BaseResponse<String>> getRedOperatorIdentity();

    @POST("duoduo/customer/getCustomerBankInfo")
    Observable<BaseResponse<GetCustomerBankInfoResponse>> getCustomerBankInfo();

    @POST("duoduo/walletBalance/cnyRecharge")
    Observable<BaseResponse<CnyRechargeResponse>> cnyRecharge();

    @POST("duoduo/customer/isBandBankInfo")
    Observable<BaseResponse<String>> isBandBankInfo();

    @POST("duoduo/walletBalance/cnyRechargeConfirm")
    @FormUrlEncoded
    Observable<BaseResponse<String>> cnyRechargeConfirm(@Field("rechargeNum") String rechargeNum, @Field("remark") String remark, @Field("picture") String picture);

    @POST("duoduo/customer/addCustomerBankInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> addCustomerBankInfo(@Field("bankInfo") String bankInfo);

    @POST("duoduo/walletBalance/cnyWithdraw")
    @FormUrlEncoded
    Observable<BaseResponse<String>> cnyWithdraw(@Field("withdrawNum") String withdrawNum, @Field("payPwd") String payPwd);

    @POST("duoduo/customer/deleteCustomerBankInfo")
    @FormUrlEncoded
    Observable<BaseResponse<String>> deleteCustomerBankInfo(@Field("payPwd") String payPwd);

    @POST("duoduo/order/addSubmitOrder")
    @FormUrlEncoded
    Observable<BaseResponse<String>> addSubmitOrder(@Field("payPwd") String payPwd, @Field("qrcode") String qrcode, @Field("payAmount") String payAmount
            , @Field("currency") String currency);

    @POST("duoduo/customer/getCustomerIdentity")
    Observable<BaseResponse<GetCustomerIdentity>> getCustomerIdentity();

    @FormUrlEncoded
    @POST("duoduo/exchange/userBuy")
    Observable<BaseResponse<GetOrderInfoById>> userBuy(@Field("nonce") String nonce,
                                                       @Field("number") String number,
                                                       @Field("currency") String currency,
                                                       @Field("payType") String payType);

    @FormUrlEncoded
    @POST("duoduo/exchange/acceptorBuy")
    Observable<BaseResponse<String>> acceptorBuy(@Field("currency") String nonce,
                                                 @Field("maxNum") String number,
                                                 @Field("minNum") String currency,
                                                 @Field("nonce") String payType,
                                                 @Field("number") String payPwd,
                                                 @Field("payPwd") String price,
                                                 @Field("payType") String minNum,
                                                 @Field("price") String maxNum);

    @FormUrlEncoded
    @POST("duoduo/exchange/acceptorSell")
    Observable<BaseResponse<String>> acceptorSell(@Field("currency") String nonce,
                                                  @Field("maxNum") String number,
                                                  @Field("minNum") String currency,
                                                  @Field("nonce") String payType,
                                                  @Field("number") String payPwd,
                                                  @Field("payPwd") String price,
                                                  @Field("payType") String minNum,
                                                  @Field("price") String maxNum,
                                                  @Field("rate") String rate);

    @FormUrlEncoded
    @POST("duoduo/exchange/acceptorCloseSell")
    Observable<BaseResponse<String>> acceptorCloseSell(@Field("sellOrderId") String sellOrderId,
                                                       @Field("nonce") String nonce);

    @FormUrlEncoded
    @POST("duoduo/exchange/acceptorCancelBuy")
    Observable<BaseResponse<String>> acceptorCancelBuy(@Field("nonce") String buyOrderId,
                                                       @Field("buyOrderId") String nonce);

    @FormUrlEncoded
    @POST("duoduo/exchange/userSell")
    Observable<BaseResponse<UserSellResponse>> userSell(@Field("nonce") String nonce,
                                                        @Field("number") String number,
                                                        @Field("currency") String currency,
                                                        @Field("payType") String payType);

    @FormUrlEncoded
    @POST("duoduo/exchange/confirmUserSell")
    Observable<BaseResponse<GetOrderInfoById>> confirmUserSell(@Field("buyOrderId") String buyOrderId,
                                                               @Field("payType") String payType,
                                                               @Field("nonce") String nonce,
                                                               @Field("number") String number,
                                                               @Field("payPwd") String payPwd);

    @FormUrlEncoded
    @POST("duoduo/exchange/getBuyList")
    Observable<BaseResponse<List<GetBuyList>>> getBuyList(@Field("currency") String currency,
                                                          @Field("nonce") String nonce,
                                                          @Field("pageNoStr") String pageNoStr,
                                                          @Field("pageSizeStr") String pageSizeStr);

    @FormUrlEncoded
    @POST("duoduo/exchange/getSellList")
    Observable<BaseResponse<List<GetBuyList>>> getSellList(@Field("currency") String currency,
                                                           @Field("nonce") String nonce,
                                                           @Field("pageNoStr") String pageNoStr,
                                                           @Field("pageSizeStr") String pageSizeStr);

    @FormUrlEncoded
    @POST("duoduo/exchange/userConfirmBuy")
    Observable<BaseResponse<GetOrderInfoById>> userConfirmBuy(@Field("sellOrderId") String sellOrderId,
                                                              @Field("payType") String payType,
                                                              @Field("nonce") String nonce,
                                                              @Field("number") String number);

    @FormUrlEncoded
    @POST("duoduo/exchange/userConfirmPay")
    Observable<BaseResponse<String>> userConfirmPay(@Field("bothOrderId") String bothOrderId,
                                                    @Field("nonce") String nonce,
                                                    @Field("payPwd") String payPwd);

    @FormUrlEncoded
    @POST("duoduo/exchange/addAppeal")
    Observable<BaseResponse<String>> addAppeal(@Field("appealReason") String appealReason,
                                               @Field("bothOrderId") String bothOrderId,
                                               @Field("nonce") String nonce,
                                               @Field("picture") String picture,
                                               @Field("plaintiffId") String plaintiffId);

    @FormUrlEncoded
    @POST("duoduo/exchange/userCancelledBuy")
    Observable<BaseResponse<String>> userCancelledBuy(@Field("bothOrderId") String bothOrderId,
                                                      @Field("nonce") String nonce);

    @FormUrlEncoded
    @POST("duoduo/exchange/userConfirmDeposit")
    Observable<BaseResponse<String>> userConfirmDeposit(@Field("bothOrderId") String bothOrderId,
                                                        @Field("nonce") String nonce,
                                                        @Field("payPwd") String payPwd);

    @FormUrlEncoded
    @POST("duoduo/exchange/acceptorConfirmDeposit")
    Observable<BaseResponse<String>> acceptorConfirmDeposit(@Field("bothOrderId") String bothOrderId,
                                                            @Field("nonce") String nonce,
                                                            @Field("payPwd") String payPwd);

    @FormUrlEncoded
    @POST("duoduo/exchange/acceptorConfirmPay")
    Observable<BaseResponse<String>> acceptorConfirmPay(@Field("bothOrderId") String bothOrderId,
                                                        @Field("nonce") String nonce,
                                                        @Field("payPwd") String payPwd);

    @POST("duoduo/walletBalance/getCnyWithdrawRate")
    Observable<BaseResponse<String>> getCnyWithdrawRate();


    @FormUrlEncoded
    @POST("duoduo/ecology/getEcologyByType")
    Observable<BaseResponse<List<GetEcologyByType>>> getEcologyByType(@Field("type") String type);


    @FormUrlEncoded
    @POST("duoduo/exchange/acceptorCancelPay")
    Observable<BaseResponse<String>> acceptorCancelPay(@Field("bothOrderId") String bothOrderId,
                                                      @Field("nonce") String nonce);


}