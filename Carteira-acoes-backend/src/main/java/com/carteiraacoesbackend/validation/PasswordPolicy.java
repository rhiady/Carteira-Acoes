package com.carteiraacoesbackend.validation;

public final class PasswordPolicy {

  public static final String PATTERN = "^(?=.*[a-z])(?=.*[A-Z]).{8,72}$";

  private PasswordPolicy() {}
}
