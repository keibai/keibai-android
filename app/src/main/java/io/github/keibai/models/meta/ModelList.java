package io.github.keibai.models.meta;


import java.util.List;

import io.github.keibai.models.Model;
import io.github.keibai.models.ModelAbstract;

public class ModelList<T extends ModelAbstract> implements Model {
    public List<T> list;
}
