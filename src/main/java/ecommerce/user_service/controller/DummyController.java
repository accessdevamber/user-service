package ecommerce.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy")
@Slf4j
@Tag(
        name = "Dummy APIs",
        description = "APIs used for basic application testing"
)
public class DummyController {


    @Operation(
            summary = "Get dummy message",
            description = "Returns a simple message from User Service"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Dummy message returned successfully"
    )
    @GetMapping()
    String dummyMessage() {
        log.info("Hello from dummy endpoint");
        return "Hello from dummy endpoint";
    }
}


//@Tag
// ↓
//describes/groups the whole controller
//
//@Operation
// ↓
//describes one endpoint
//
//@ApiResponse
// ↓
//documents a possible HTTP response


//==============CURL EXPLANATION================

//Understanding the generated curl
//Swagger shows:
//curl -X 'POST' \
//  'http://localhost:9090/users/createUser/V2' \
//  -H 'accept: */*' \
//  -H 'Idempotency-Key: kishan-001' \
//  -H 'Content-Type: application/json' \
//  -d '{
//    "firstName": "kishan",
//    "lastName": "ishan",
//    "email": "ishankishan@example.com",
//    "phone": "+3123355403462",
//    "password": "stringst"
//  }'
//
//Swagger isn't doing anything magical here. When you click Execute, it constructs an HTTP request. The curl block is basically showing you the equivalent command-line request.
//-X 'POST' means:
//-X = HTTP method
//
//POST
//
//So:
//curl -X POST
//
//means "make a POST request."
//Then:
//'http://localhost:9090/users/createUser/V2'
//
//is simply the destination:
//localhost
//   ↓
//port 9090
//   ↓
/// users
//   ↓
///createUser/V2
//
//-H 'accept: */*'
//-H means HTTP header.
//Accept: */*
//
//is the client telling the server:
//I can accept any response media type.
//
//The format is:
//type/subtype
//
//Examples:
//application/json
//text/plain
//text/html
//image/png
//
//And:
//*/*
//
//means:
//any type / any subtype
//
//So Swagger is essentially saying:
//Give me whatever response representation this endpoint produces.
//
//Later we can make the OpenAPI definition more precise so Swagger shows:
//Accept: application/json
//
//-H 'Idempotency-Key: kishan-001'
//This comes directly from:
//@RequestHeader("Idempotency-Key") String idempotencyKey
//
//Springdoc saw @RequestHeader and Swagger created the input box you filled with:
//kishan-001
//
//Swagger then converted that into the HTTP header:
//Idempotency-Key: kishan-001
//
//So Spring receives:
//idempotencyKey = "kishan-001";
//
//This is a nice demonstration of the mapping:
//Swagger input box
//      ↓
//Idempotency-Key: kishan-001
//      ↓
//HTTP request
//      ↓
//@RequestHeader("Idempotency-Key")
//      ↓
//String idempotencyKey
//      ↓
//"kishan-001"
//
//-H 'Content-Type: application/json'
//This header describes the request body.
//Content-Type: application/json
//
//means:
//The data I'm sending you is JSON.
//
//And immediately after that Swagger sends:
//{
//  "firstName": "kishan",
//  "lastName": "ishan",
//  "email": "ishankishan@example.com",
//  "phone": "+3123355403462",
//  "password": "stringst"
//}
//
//Spring therefore knows Jackson should deserialize that JSON into:
//@RequestBody UserRequest request
//
//Conceptually:
//Content-Type: application/json
//              ↓
//          JSON body
//              ↓
//           Jackson
//              ↓
//        UserRequest
//
//A useful distinction:
//Content-Type
//= what I am SENDING you
//
//Accept
//= what I want/allow you to SEND BACK
//
//-d '{ ... }'
//-d in curl means send data/request body.
//So:
//-d '{
//   "firstName": "kishan"
//}'
//
//is the actual HTTP request body.
//Putting everything together:
//-X POST
//   ↓
//Which HTTP operation?
//
//URL
//   ↓
//Where?
//
//-H Accept
//   ↓
//What response format can I accept?
//
//-H Idempotency-Key
//   ↓
//Your custom application header
//
//-H Content-Type
//   ↓
//What format is my request body?
//
//-d
//   ↓
//Actual request body
//
//Now your response headers
//You received:
//connection: keep-alive
//content-type: application/json
//date: Sat, 03 Oct 2026 07:18:20 GMT
//keep-alive: timeout=60
//transfer-encoding: chunked
//
//These are response headers from the server, not the headers Swagger sent.
//content-type: application/json
//Probably the most important one.
//Your response body is:
//{
//  "id": 34482,
//  "firstName": "kishan",
//  "lastName": "ishan",
//  ...
//}
//
//The server tells the client:
//Content-Type: application/json
//
//meaning:
//Interpret these response bytes as JSON.
//
//Notice how it mirrors the request side:
//REQUEST
//
//Content-Type: application/json
//        ↓
//client is sending JSON
//
//
//RESPONSE
//
//Content-Type: application/json
//        ↓
//server is returning JSON
//
//date
//date: Sat, 03 Oct 2026 07:18:20 GMT
//
//The HTTP server provides the time at which the response was generated/sent, expressed here in GMT.
//Nothing application-specific.
//connection: keep-alive
//This is about the underlying network connection.
//Without connection reuse, conceptually:
//Request 1
//connect TCP
//request
//response
//close
//
//Request 2
//connect TCP AGAIN
//request
//response
//close
//
//With keep-alive:
//TCP connection
//      ↓
//Request 1 → Response 1
//      ↓
//connection remains available
//      ↓
//Request 2 → Response 2
//      ↓
//Request 3 → Response 3
//
//Reusing connections avoids repeatedly establishing new connections.
//keep-alive: timeout=60
//Related to the previous header.
//Conceptually:
//connection: keep-alive
//keep-alive: timeout=60
//
//means the connection may remain available for reuse, subject to the server's keep-alive timeout of 60 seconds.
//It doesn't mean your REST API request took 60 seconds.
//transfer-encoding: chunked
//This concerns how the HTTP response body is transferred.
//Instead of saying up front:
//Content-Length: 287
//
//the server can transfer the response as chunks:
//chunk 1
//chunk 2
//chunk 3
//...
//end
//
//Hence:
//Transfer-Encoding: chunked
//
//This is transport-level behavior and normally something your Spring business code doesn't need to care about.
//Request vs response picture
//Your Swagger execution can now be viewed as:
//SWAGGER / CLIENT
//       |
//       | POST /users/createUser/V2
//       |
//       | Accept: */*
//       | Idempotency-Key: kishan-001
//       | Content-Type: application/json
//       |
//       | {
//       |   "firstName": "kishan",
//       |   ...
//       | }
//       |
//       ↓
//+----------------------+
//|     USER SERVICE     |
//|                      |
//|   UserController     |
//|        ↓             |
//| Idempotent service   |
//|        ↓             |
//|      MySQL           |
//+----------------------+
//       |
//       | HTTP 201 Created
//       |
//       | Content-Type: application/json
//       | Connection: keep-alive
//       | ...
//       |
//       | {
//       |   "id": 34482,
//       |   "status": "ACTIVE",
//       |   "role": "CUSTOMER",
//       |   ...
//       | }
//       ↓
//SWAGGER / CLIENT
