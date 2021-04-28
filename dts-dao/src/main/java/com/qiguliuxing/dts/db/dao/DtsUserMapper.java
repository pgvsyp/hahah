package com.qiguliuxing.dts.db.dao;

import com.qiguliuxing.dts.db.domain.DtsUser;
import com.qiguliuxing.dts.db.domain.DtsUserExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DtsUserMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    long countByExample(DtsUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    int deleteByExample(DtsUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    int insert(DtsUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    int insertSelective(DtsUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    DtsUser selectOneByExample(DtsUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    DtsUser selectOneByExampleSelective(@Param("example") DtsUserExample example, @Param("selective") DtsUser.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    List<DtsUser> selectByExampleSelective(@Param("example") DtsUserExample example, @Param("selective") DtsUser.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    List<DtsUser> selectByExample(DtsUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    DtsUser selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") DtsUser.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    DtsUser selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    DtsUser selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") DtsUser record, @Param("example") DtsUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") DtsUser record, @Param("example") DtsUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(DtsUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(DtsUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByExample(@Param("example") DtsUserExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table dts_user
     *
     * @mbg.generated
     * @project https://github.com/itfsw/mybatis-generator-plugin
     */
    int logicalDeleteByPrimaryKey(Integer id);

    public DtsUser findUserId(Integer id);
}