package com.selloum.dto;

import com.selloum.entity.Setmeal;
import com.selloum.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
