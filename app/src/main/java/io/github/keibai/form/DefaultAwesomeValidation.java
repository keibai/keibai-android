package io.github.keibai.form;

import android.content.Context;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

public class DefaultAwesomeValidation extends AwesomeValidation {

    public DefaultAwesomeValidation(Context context) {
        super(ValidationStyle.UNDERLABEL);
        this.setContext(context);
    }
}
