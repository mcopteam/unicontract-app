package com.uniledger.contract.dao;

import com.uniledger.contract.model.CompanyUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/6/10 13:31.
 */
@Repository
public interface CompanyUserMapper {
    /**
     * 根据条件查询用户信息
     * @param pubkey
     * @return
     */
    CompanyUser getCompanyUserByCompanyUserPubkey(@Param("pubkey")String pubkey);

    /**
     * 根据用户id, name, nameEn, 状态获取信息!
     * @param companyUser {@link CompanyUser}
     * @return  {@link  List <CompanyUser>}
     */
    List<CompanyUser> getCompanyUserListByCompanyUser(@Param("companyUser")CompanyUser companyUser);

    /**
     * 根据用户id, name, nameEn,pubkey,status  状态获取信息!
     * @param id
     * @param name
     * @param nameEn
     * @param pubkey
     * @param status
     * @return
     */
    List<CompanyUser> getCompanyUserList(@Param("id")Long id, @Param("name")String name, @Param("nameEn")String nameEn,
                                         @Param("pubkey")String pubkey, @Param("status")Short ...status);

    /**
     * 根据id获取公司用户
     * @param id 用户id
     * @return {@link CompanyUser}
     */
    CompanyUser getCompanyUser(@Param("id") Long id);
}
