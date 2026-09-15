package com.carteiraacoesbackend.services;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TaxaUsdBrl(BigDecimal valor, String fonte, OffsetDateTime dataHoraReferencia) {}
