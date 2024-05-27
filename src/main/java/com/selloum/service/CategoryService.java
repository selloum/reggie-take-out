package com.selloum.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.selloum.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
