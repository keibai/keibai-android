package io.github.keibai.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import io.github.keibai.R;

public class Gravatar {
    String GRAVATAR_BASE_URL = "https://www.gravatar.com/avatar";

    private String email;
    private int size;

    public Gravatar(String email) {
        this.email = email;
        this.size = 10;
    }

    public Gravatar setSize(int size) {
        this.size = size;
        return this;
    }

    public String generateUrl() {
        String md5Email = new String(Hex.encodeHex(DigestUtils.md5(email)));
        return String.format("%s/%s?s=%d", GRAVATAR_BASE_URL, md5Email, R.dimen.profile_gravatar_size);
    }

}
