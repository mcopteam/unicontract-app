package com.uniledger.contract.service.impl;

import com.github.pagehelper.PageHelper;
import com.uniledger.contract.common.PageInfo;
import com.uniledger.contract.dao.CompanyUserMapper;
import com.uniledger.contract.dao.ContractMapper;
import com.uniledger.contract.model.CompanyUser;
import com.uniledger.contract.service.CompanyUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wxcsdb88 on 2017/6/10 13:31.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CompanyUserServiceImpl implements CompanyUserService{
    @Autowired
    private CompanyUserMapper companyUserMapper;

    @Override
    public CompanyUser getCompanyUserByCompanyUserPubkey(String pubkey) {
        return companyUserMapper.getCompanyUserByCompanyUserPubkey(pubkey);
    }

    @Override
    public List<CompanyUser> getCompanyUserListByCompanyUser(CompanyUser companyUser) {
        return companyUserMapper.getCompanyUserListByCompanyUser(companyUser);
    }

    @Override
    public PageInfo<CompanyUser> getCompanyUserListByCompanyUserPageInfo(Integer pageNum, Integer pageSize, CompanyUser companyUser) {
        PageHelper.startPage(pageNum, pageSize);
        return  new PageInfo(companyUserMapper.getCompanyUserListByCompanyUser(companyUser));
    }

    @Override
    public List<CompanyUser> getCompanyUserList(Long id, String name, String nameEn, String pubkey, Short... status) {
        return companyUserMapper.getCompanyUserList(id, name, nameEn, pubkey, status);
    }

    @Override
    public PageInfo<CompanyUser> getCompanyUserListPageInfo(Integer pageNum, Integer pageSize, Long id, String name, String nameEn, String pubkey, Short... status) {
        PageHelper.startPage(pageNum, pageSize);
        return  new PageInfo(companyUserMapper.getCompanyUserList(id, name, nameEn, pubkey, status));
    }

    @Override
    public CompanyUser getCompanyUser(Long id) {
        return companyUserMapper.getCompanyUser(id);
    }
}
