package cn.example.user.mapper;

import cn.example.common.entity.AiCallLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AiCallLogMapper {

    @Select("SELECT * FROM ai_call_log ORDER BY create_time DESC LIMIT #{limit}")
    List<AiCallLog> findRecent(@Param("limit") Integer limit);

    @Select("SELECT COUNT(*) FROM ai_call_log WHERE is_success = 1")
    int countSuccess();

    @Select("SELECT COUNT(*) FROM ai_call_log WHERE is_success = 0")
    int countFailed();

    @Insert("INSERT INTO ai_call_log(user_id, plan_id, model, prompt, response, " +
            "token_count, duration_ms, is_success, error_msg) " +
            "VALUES(#{userId}, #{planId}, #{model}, #{prompt}, #{response}, " +
            "#{tokenCount}, #{durationMs}, #{isSuccess}, #{errorMsg})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(AiCallLog log);
}