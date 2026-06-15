package com.realworld.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.realworld.blog.dto.response.ListTagsResponse;
import com.realworld.blog.entity.Tag;
import com.realworld.blog.service.TagService;
import com.realworld.blog.mapper.TagMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author jiaolei
* @description 针对表【tag】的数据库操作Service实现
* @createDate 2026-06-08 19:43:30
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{


    @Override
    public ListTagsResponse tagList() {
        List<Tag> list = this.list();
        List<String> tagNameList = list.stream().map(tag -> tag.getName()).toList();
        ListTagsResponse listTagsResponse = new ListTagsResponse();
        listTagsResponse.setTags(tagNameList);
        return listTagsResponse;
    }
}




