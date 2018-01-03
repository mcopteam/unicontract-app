package com.uniledger.contract.service;

import com.uniledger.contract.common.PageInfo;
import com.uniledger.contract.model.CompanyUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/6/10 13:02.
 */
public interface CompanyUserService {

    /**
     * 根据条件查询用户信息
     * @param pubkey
     * @return
     */
    CompanyUser getCompanyUserByCompanyUserPubkey(String pubkey);

    /**
     * 根据用户id, name, nameEn, 状态获取信息!
     * @param companyUser {@link CompanyUser}
     * @return  {@link  List<CompanyUser>}
     */
    List<CompanyUser> getCompanyUserListByCompanyUser(CompanyUser companyUser);

    /**
     * 分页获取公司用户信息
     * @param pageNum 当前页码
     * @param pageSize 页面记录数
     * @param companyUser {@link CompanyUser}
     * @return {@link  PageInfo<CompanyUser>}
     */
    PageInfo<CompanyUser> getCompanyUserListByCompanyUserPageInfo(Integer pageNum, Integer pageSize, CompanyUser companyUser);

    /**
     * @param id
     * @param name
     * @param nameEn
     * @param pubkey
     * @param status
     * @return
     */
    List<CompanyUser> getCompanyUserList(Long id, String name, String nameEn, String pubkey, Short ...status);

    /**
     * 根据用户id, name, nameEn,pubkey,status  状态获取信息!
     * @param pageNum
     * @param pageSize
     * @param id
     * @param name
     * @param nameEn
     * @param pubkey
     * @param status
     * @return
     */
    PageInfo<CompanyUser> getCompanyUserListPageInfo(Integer pageNum, Integer pageSize, Long id, String name, String nameEn, String pubkey, Short ...status);

    /**
     * 根据id获取公司用户
     * @param id 用户id
     * @return {@link CompanyUser}
     */
    CompanyUser getCompanyUser(Long id);


}
