package com.jarvis.dto;


import com.jarvis.entity.Setmeal;
import com.jarvis.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
