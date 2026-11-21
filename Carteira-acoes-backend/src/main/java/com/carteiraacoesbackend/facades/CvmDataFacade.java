package com.carteiraacoesbackend.facades;

import com.carteiraacoesbackend.clients.CvmDataClient;
import com.carteiraacoesbackend.dto.integrations.BrasilApiCvmBrokerResponse;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.ExternalCallLogger;
import feign.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipInputStream;
import org.springframework.stereotype.Component;

@Component
public class CvmDataFacade {
  private final CvmDataClient client;
  private final ExternalCallLogger externalCallLogger;

  public CvmDataFacade(CvmDataClient client, ExternalCallLogger externalCallLogger) {
    this.client = client;
    this.externalCallLogger = externalCallLogger;
  }

  public BrasilApiCvmBrokerResponse consultarCorretora(String cnpj) {
    try {
      return externalCallLogger.execute("cvm", "intermediarios", cnpj, () -> consultar(cnpj));
    } catch (ApiException exception) {
      throw exception;
    } catch (Exception exception) {
      throw ApiException.external(
          org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
          "EXTERNAL_API_UNAVAILABLE",
          "Não foi possível consultar o cadastro da CVM.");
    }
  }

  private BrasilApiCvmBrokerResponse consultar(String cnpj) {
    try (Response response = client.baixarCadastroIntermediarios();
        ZipInputStream zip = new ZipInputStream(response.body().asInputStream());
        BufferedReader reader = abrirCadastro(zip)) {
      String cabecalho = reader.readLine();
      int tipoIndex = indice(cabecalho, "TP_PARTIC");
      int cnpjIndex = indice(cabecalho, "CNPJ");
      int codigoIndex = indice(cabecalho, "CD_CVM");
      int statusIndex = indice(cabecalho, "SIT");
      String linha;
      while ((linha = reader.readLine()) != null) {
        String[] campos = linha.split(";", -1);
        if (campos.length > Math.max(tipoIndex, Math.max(cnpjIndex, Math.max(codigoIndex, statusIndex)))
            && "CORRETORAS".equals(campos[tipoIndex])
            && limpar(campos[cnpjIndex]).equals(cnpj)) {
          return new BrasilApiCvmBrokerResponse(
              campos[cnpjIndex], campos[codigoIndex], campos[statusIndex]);
        }
      }
      throw ApiException.notFound(
          "EXTERNAL_RESOURCE_NOT_FOUND", "Corretora não encontrada no cadastro da CVM.");
    } catch (ApiException exception) {
      throw exception;
    } catch (Exception exception) {
      throw new IllegalStateException(exception);
    }
  }

  private BufferedReader abrirCadastro(ZipInputStream zip) throws java.io.IOException {
    java.util.zip.ZipEntry entry;
    while ((entry = zip.getNextEntry()) != null) {
      if ("cad_intermed.csv".equals(entry.getName()))
        return new BufferedReader(new InputStreamReader(zip, StandardCharsets.UTF_8));
    }
    throw new java.io.IOException("Arquivo de cadastro não encontrado no ZIP da CVM.");
  }

  private int indice(String cabecalho, String coluna) {
    String[] colunas = cabecalho.split(";", -1);
    for (int i = 0; i < colunas.length; i++) if (coluna.equals(colunas[i])) return i;
    throw new IllegalStateException("Coluna " + coluna + " não encontrada no cadastro da CVM.");
  }

  private String limpar(String valor) {
    return valor == null ? "" : valor.replaceAll("\\D", "");
  }
}
