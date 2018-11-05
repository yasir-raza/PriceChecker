package com.technosysint.pricechecker.DBHelper;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import org.json.JSONObject;

/**
 * Created by Yasir.Raza on 10/23/2018.
 */

@Entity
public class User {
    @PrimaryKey
    private int UserId;
    private int CompanyBranchId;
    private int EmployeeInformationId;
    private int WebUserLogId;
    private int CompanyId;
    private int CompanyBranchTypeId;
    private int DataEntryStatus;
    private int UserLogId;
    private String EmployeeName;
    private String LoginId;
    private String Password;
    private String BranchName;
    private String BranchCode;
    private String CompanyName;
    private String GUID;
    private boolean IsAlreadyLogin;
    private byte[] BranchLogo;


    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public int getCompanyBranchId() {
        return CompanyBranchId;
    }

    public void setCompanyBranchId(int companyBranchId) {
        CompanyBranchId = companyBranchId;
    }

    public int getEmployeeInformationId() {
        return EmployeeInformationId;
    }

    public void setEmployeeInformationId(int employeeInformationId) {
        EmployeeInformationId = employeeInformationId;
    }

    public int getWebUserLogId() {
        return WebUserLogId;
    }

    public void setWebUserLogId(int webUserLogId) {
        WebUserLogId = webUserLogId;
    }

    public int getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(int companyId) {
        CompanyId = companyId;
    }

    public int getCompanyBranchTypeId() {
        return CompanyBranchTypeId;
    }

    public void setCompanyBranchTypeId(int companyBranchTypeId) {
        CompanyBranchTypeId = companyBranchTypeId;
    }

    public int getDataEntryStatus() {
        return DataEntryStatus;
    }

    public void setDataEntryStatus(int dataEntryStatus) {
        DataEntryStatus = dataEntryStatus;
    }

    public int getUserLogId() {
        return UserLogId;
    }

    public void setUserLogId(int userLogId) {
        UserLogId = userLogId;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getLoginId() {
        return LoginId;
    }

    public void setLoginId(String loginId) {
        LoginId = loginId;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getBranchName() {
        return BranchName;
    }

    public void setBranchName(String branchName) {
        BranchName = branchName;
    }

    public String getBranchCode() {
        return BranchCode;
    }

    public void setBranchCode(String branchCode) {
        BranchCode = branchCode;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public boolean getIsAlreadyLogin() {
        return IsAlreadyLogin;
    }

    public void setIsAlreadyLogin(boolean alreadyLogin) {
        IsAlreadyLogin = alreadyLogin;
    }

    public byte[] getBranchLogo() {
        return BranchLogo;
    }

    public void setBranchLogo(byte[] branchLogo) {
        BranchLogo = branchLogo;
    }

    public void ConvertJsonToUserLogin(String json)
    {
        try {
            JSONObject jObj = new JSONObject(json);
            this.setBranchCode(jObj.getString("BranchCode"));
            this.setBranchName(jObj.getString("BranchName"));
            this.setCompanyName(jObj.getString("CompanyName"));
            this.setEmployeeName(jObj.getString("EmployeeName"));
            this.setLoginId(jObj.getString("LoginId"));
            this.setPassword(jObj.getString("Password"));
            this.setGUID(jObj.getString("GUID"));
            this.setCompanyBranchId(jObj.getInt("CompanyBranchId"));
            this.setCompanyId(jObj.getInt("CompanyId"));
            this.setDataEntryStatus(jObj.getInt("DataEntryStatus"));
            this.setUserId(jObj.getInt("UserId"));
            this.setUserLogId(jObj.getInt("UserLogId"));
            this.setWebUserLogId(jObj.getInt("WebUserLogId"));
            this.setCompanyBranchTypeId(jObj.getInt("CompanyBranchTypeId"));
            Integer sEmployeeInformationId = jObj.getInt("EmployeeInformationId");
            if (sEmployeeInformationId != null) {
                this.setEmployeeInformationId(sEmployeeInformationId);
            }
            Boolean sIsAlreadyLogin = jObj.getBoolean("IsAlreadyLogin");
            if (sIsAlreadyLogin != null) {
                this.setIsAlreadyLogin(sIsAlreadyLogin);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
