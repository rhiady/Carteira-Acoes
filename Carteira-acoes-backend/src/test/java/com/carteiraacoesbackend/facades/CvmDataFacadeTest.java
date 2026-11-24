package com.carteiraacoesbackend.facades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.carteiraacoesbackend.clients.CvmDataClient;
import com.carteiraacoesbackend.exceptions.ApiException;
import com.carteiraacoesbackend.integrations.ExternalCallLogger;
import feign.Request;
import feign.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;

class CvmDataFacadeTest {

  @Test
  void findsFormattedCnpjInTheExpectedCsvEntry() {
    CvmDataClient client =
        clientWith(zip("cad_intermed.csv", csv("CORRETORAS;12.345.678/0001-90;123;ATIVA")));

    var response =
        new CvmDataFacade(client, new ExternalCallLogger()).consultarCorretora("12345678000190");

    assertEquals("12.345.678/0001-90", response.cnpj());
    assertEquals("123", response.codigoCvm());
    assertEquals("ATIVA", response.status());
  }

  @Test
  void skipsOtherTypesAndReturnsNotFoundWhenCnpjDoesNotMatch() {
    CvmDataClient client =
        clientWith(zip("cad_intermed.csv", csv("BANCO;12345678000190;123;ATIVA")));

    ApiException error =
        assertThrows(
            ApiException.class,
            () ->
                new CvmDataFacade(client, new ExternalCallLogger())
                    .consultarCorretora("12345678000190"));

    assertEquals("EXTERNAL_RESOURCE_NOT_FOUND", error.getCode());
  }

  @Test
  void skipsRowsWithTooFewColumns() {
    CvmDataClient client =
        clientWith(zip("cad_intermed.csv", "TP_PARTIC;CNPJ;CD_CVM;SIT\nCORRETORAS\n"));

    ApiException error =
        assertThrows(
            ApiException.class,
            () ->
                new CvmDataFacade(client, new ExternalCallLogger())
                    .consultarCorretora("12345678000190"));

    assertEquals("EXTERNAL_RESOURCE_NOT_FOUND", error.getCode());
  }

  @Test
  void rejectsZipWithoutExpectedEntry() {
    CvmDataClient client = clientWith(zip("other.csv", "header\nvalue\n"));

    ApiException error =
        assertThrows(
            ApiException.class,
            () ->
                new CvmDataFacade(client, new ExternalCallLogger())
                    .consultarCorretora("12345678000190"));

    assertEquals("EXTERNAL_API_UNAVAILABLE", error.getCode());
  }

  @Test
  void skipsUnrelatedZipEntriesBeforeReadingTheCadastroFile() {
    CvmDataClient client =
        clientWith(
            zipWithEntries(
                new String[][] {
                  {"README.txt", "ignore"},
                  {"cad_intermed.csv", csv("CORRETORAS;12345678000190;123;ATIVA")}
                }));

    assertEquals(
        "123",
        new CvmDataFacade(client, new ExternalCallLogger())
            .consultarCorretora("12345678000190")
            .codigoCvm());
  }

  @Test
  void rejectsMalformedHeaderAndMalformedZipAsUnavailable() {
    CvmDataClient missingColumn =
        clientWith(
            zip("cad_intermed.csv", "TIPO;CNPJ;CD_CVM;SIT\nCORRETORAS;12345678000190;123;ATIVA\n"));
    ApiException headerError =
        assertThrows(
            ApiException.class,
            () ->
                new CvmDataFacade(missingColumn, new ExternalCallLogger())
                    .consultarCorretora("12345678000190"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", headerError.getCode());

    CvmDataClient malformedZip = mock(CvmDataClient.class);
    when(malformedZip.baixarCadastroIntermediarios()).thenReturn(response("not-a-zip"));
    ApiException zipError =
        assertThrows(
            ApiException.class,
            () ->
                new CvmDataFacade(malformedZip, new ExternalCallLogger())
                    .consultarCorretora("12345678000190"));
    assertEquals("EXTERNAL_API_UNAVAILABLE", zipError.getCode());
  }

  @Test
  void propagatesProviderApiErrors() {
    CvmDataClient client = mock(CvmDataClient.class);
    when(client.baixarCadastroIntermediarios())
        .thenThrow(
            ApiException.external(
                org.springframework.http.HttpStatus.BAD_GATEWAY, "EXTERNAL_API_ERROR", "falha"));

    ApiException error =
        assertThrows(
            ApiException.class,
            () ->
                new CvmDataFacade(client, new ExternalCallLogger())
                    .consultarCorretora("12345678000190"));

    assertEquals("EXTERNAL_API_ERROR", error.getCode());
  }

  private CvmDataClient clientWith(byte[] content) {
    CvmDataClient client = mock(CvmDataClient.class);
    when(client.baixarCadastroIntermediarios()).thenReturn(response(content));
    return client;
  }

  private Response response(byte[] content) {
    return Response.builder().status(200).request(request()).body(content).build();
  }

  private Response response(String content) {
    return Response.builder()
        .status(200)
        .request(request())
        .body(content, StandardCharsets.UTF_8)
        .build();
  }

  private Request request() {
    return Request.create(
        Request.HttpMethod.GET, "http://cvm.test", java.util.Map.of(), null, null, null);
  }

  private byte[] zip(String name, String content) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      try (ZipOutputStream zip = new ZipOutputStream(output)) {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
      }
      return output.toByteArray();
    } catch (IOException exception) {
      throw new AssertionError(exception);
    }
  }

  private byte[] zipWithEntries(String[][] entries) {
    try {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      try (ZipOutputStream zip = new ZipOutputStream(output)) {
        for (String[] entry : entries) {
          zip.putNextEntry(new ZipEntry(entry[0]));
          zip.write(entry[1].getBytes(StandardCharsets.UTF_8));
          zip.closeEntry();
        }
      }
      return output.toByteArray();
    } catch (IOException exception) {
      throw new AssertionError(exception);
    }
  }

  private String csv(String row) {
    return "TP_PARTIC;CNPJ;CD_CVM;SIT\n" + row + "\n";
  }
}
