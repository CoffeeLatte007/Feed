package com.feed.mapper;

import com.feed.model.FeedModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2016/8/1.
 */
public interface FeedModelMapper {
    FeedModel getFeedList(@Param("uuid")Integer uuid);
}
