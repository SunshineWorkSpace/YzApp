package com.yingshixiezuovip.yingshi.datautils;

public enum TaskType {
    TASK_TYPE_STARTUP,//startpage.spr
    TASK_TYPE_SendSMS,// /send.spr
    TASK_TYPE_REGIST,///register.spr
    TASK_TYPE_LOGIN,// /login.spr
    TASK_TYPE_HOME_TYPE,// /types.spr
    TASK_TYPE_HOME_LIST,// /starlist.spr
    TASK_TYPE_DO_COMMENT,//zan.spr
    TASK_TYPE_COMMENT_LIST,//zanlist.spr
    TASK_TYPE_HOME_FOLLOW,//follow.spr
    TASK_TYPE_HOME_CLEAR_FOLLOW,//cancelfollow.spr
    TASK_TYPE_LOGIN_SINA,//loginforweibo.spr
    TASK_TYPE_LOGIN_WECHAT,//loginforweixin.spr
    TASK_TYPE_USER_INFO,//user/info.spr
    TASK_TYPE_RESET_SendSMS,//retsetSend.spr
    TASK_TYPE_RESET_PASSWORD,//retsetpwd.spr
    TASK_TYPE_LIST_ITEM_DETAILS,//detail.spr
    TASK_TYPE_SUBSCRIBE_STAR,//subscribeStar.spr
    TASK_TYPE_SUBSCRIBE_STAR_QRY,//getSubscribeStartime.spr
    TASK_TYPE_QRY_OTHER_USERINFO,//user/oinfo.spr
    TASK_TYPE_QRY_OTHER_WORKS,//user/ozuoping.spr
    TASK_TYPE_QRY_OTHER_FOLLOW_LIST,//user/ofollowlist.spr
    TASK_TYPE_QRY_SHARE_CONTENT,//ishare.spr
    TASK_TYPE_QRY_HEART_VIDEO,//videolist.spr
    TASK_TYPE_MSG_SELLER_ORDER,//msg/sellerOrderlist.spr
    TASK_TYPE_MSG_BUYER_ORDER,//orderInfo.spr
    TASK_TYPE_MSG_COMMENT_LIST,//msg/zanlist.spr
    TASK_TYPE_MSG_FANS_LIST,//user/fanslist.spr
    TASK_TYPE_MINE_WORK_LIST,//user/workslist.spr
    TASK_TYPE_MINE_COMMENT_LIST,//user/zanlist.spr
    TASK_TYPE_MINE_FOLLOW_LIST,//user/followlist.spr
    TASK_TYPE_MINE_HEAD_UPLOAD,//user/updatephoto.spr
    TASK_TYPE_MINE_BACKGROUND_UPLOAD,//user/updatebackground.spr
    TASK_TYPE_MINE_PHOTO,//user/updatephoto.spr
    TASK_TYPE_RELEASE,//release.spr
    TASK_TYPE_VIDEO_UPLOAD,//uploadvideo.spr
    TASK_TYPE_SEARCH_HOT,//search.spr
    TASK_TYPE_SEARCH_RECOMMEND,//hotrecommend.spr
    TASK_TYPE_DELETE_WORKS,//delstar.spr
    TASK_TYPE_UPDATE_WORKS_PRICE,//user/updateStarPrice.spr
    TASK_TYPE_UPDATE_USER_PRICE,//user/updateprice.spr
    TASK_TYPE_UPDATE_USER_INFO,//user/update.spr
    TASK_TYPE_QRY_CITY_INFO,//city.spr
    TASK_TYPE_UPDATE_INVITE_CODE,//user/writeInvite.spr
    TASK_TYPE_SUBMIT_AUTHENTICATION,//user/authsub.spr
    TASK_TYPE_QRY_PAY_ORDER_WECHAT,//user/identificationforwxpay.spr
    TASK_TYPE_QRY_PAY_ORDER_ALIPAY,//user/identificationforalipay.spr
    TASK_TYPE_QRY_USERINFO_BY_TOKEN,//infoForToken.spr
    TASK_TYPE_MSG_SELLER_ORDER_DETAILS,//msg/orderInfo.spr
    TASK_TYPE_QRY_ORDER_PAYINFO,//weixinpay.spr
    TASK_TYPE_BUYER_CANCEL_ORDER,//msg/cancelOrder.spr
    TASK_TYPE_QRY_ORDER_PAYINFO_WHIT_WECHAT,//weixinpay.spr
    TASK_TYPE_UPDATE_ORDER_DATE,//msg/updateOrderData.spr
    TASK_TYPE_UPDATE_ORDER_PRICE,//msg/updateOrderTotal.spr
    TASK_TYPE_MAKE_SURE_ORDER,//msg/makesureOrder.spr
    TASK_TYPE_ORDER_COMPLETE,//msg/completeOrder.spr
    TASK_TYPE_QRY_COMMENT_LIST,//reviewList.spr
    TASK_TYPE_QRY_COMMENT_DETAILS,//replyList.spr
    TASK_TYPE_DO_REVIEW,//review.spr
    TASK_TYPE_DO_REPLY,//replyreview.spr
    TASK_TYPE_RELEASE_PASS,//releasePass.spr
    TASK_TYPE_REVIEW_ZAN,//reviewzan.spr
    TASK_TYPE_REVIEW_CANCELZAN,//reviewcancelzan.spr
    TASK_TYPE_BIND_PHONE,//user/bindphone.spr
    TASK_TYPE_COMPANY_CHECK_VERIFICODE,//v1/checkcompanycode.spr
    TASK_TYPE_COMPANY_INFO_UPLOAD,//v1/uploadcompanyinfo.spr
    TASK_TYPE_COMPANY_PAY_BY_WECHAT,//v1/companyauthbondforwxpay.spr
    TASK_TYPE_COMPANY_PAY_BY_ALIPAY,//v1/companyauthbondforalipay.spr
    TASK_TYPE_USERINFO_PAY_BY_WECHAT,//v1/companyauthbondforwxpay.spr
    TASK_TYPE_USERINFO_PAY_BY_ALIPAY,//v1/companyauthbondforalipay.spr
    TASK_TYPE_SUBMIT_FEEDBACK,//v1/sendcustomermsg.spr
    TASK_TYPE_QRY_COMPANY_INFO,//v1/user/getcompanyinfo.spr
    TASK_TYPE_QRY_COMPANY_STATUS,//v1/user/infoupdatelist.spr
    TASK_TYPE_UPDADE_COMPANY_INFO,//v1/user/updatecompanyinfo.spr
    TASK_TYPE_QRY_SYS_COVER,//v1/user/zpjfmlist.spr
    TASK_TYPE_UPLOAD_COVER,//v1/user/uploadzpjfm.spr
    TASK_TYPE_UPLOAD_RESUME_VIDEO,//v1/user/uploadresumevideo.spr
    TASK_TYPE_UPLOAD_RESUME,//v1/user/uploadresume.spr
    TASK_TYPE_UPDATE_RESUME,//v1/user/v1updateresume.spr
    TASK_TYPE_UPLOAD_BYZS,//v1/user/uploadbycertificate.spr
    TASK_TYPE_CUSTOM_URL,//v1/user/updatesamplereelsurl.spr
    TASK_TYPE_QRY_USER_WORKS,///v1/v1userzpj.spr
    TASK_TYPE_QRY_VERSION,///yingzhe/sys/version.spr
    TASK_TYPE_QRY_HOT_SEARCH,///yingzhe/v1/hotsearch.spr
    TASK_TYPE_QRY_HOT_SEARCH_DETAILS,//v1/search.spr
    TASK_TYPE_STUDENT_AUTH,//v1/user/studentAuth.spr
    TASK_TYPE_QRY_STUDENT_INTO,//v1/user/getstudentinfo.spr
    TASK_TYPE_UPDATE_STUDENT_INTO,//v1/user/updatestudentinfo.spr
    TASK_TYPE_QRY_DETAILS_CHNAGE,///v1/worksUpdatePage.spr
    TASK_TYPE_UPDATE_DETAILS_CHNAGE,////v1/ worksUpdateAll.spr
    TASK_TYPE_DETAILS_RESUME,///v1/user/v1resumedetail.spr
    TASK_TYPE_UPDATE_USER_INFO_NEW,//user/update.spr
    TASK_TYPE_ISPUBLISH,//v2/isuploadindencard.spr

}