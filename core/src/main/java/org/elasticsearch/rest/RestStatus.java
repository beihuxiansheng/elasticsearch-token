begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ShardOperationFailedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableMap
import|;
end_import

begin_enum
DECL|enum|RestStatus
specifier|public
enum|enum
name|RestStatus
block|{
comment|/**      * The client SHOULD continue with its request. This interim response is used to inform the client that the      * initial part of the request has been received and has not yet been rejected by the server. The client      * SHOULD continue by sending the remainder of the request or, if the request has already been completed,      * ignore this response. The server MUST send a final response after the request has been completed.      */
DECL|enum constant|CONTINUE
name|CONTINUE
argument_list|(
literal|100
argument_list|)
block|,
comment|/**      * The server understands and is willing to comply with the client's request, via the Upgrade message header field      * (section 14.42), for a change in the application protocol being used on this connection. The server will      * switch protocols to those defined by the response's Upgrade header field immediately after the empty line      * which terminates the 101 response.      */
DECL|enum constant|SWITCHING_PROTOCOLS
name|SWITCHING_PROTOCOLS
argument_list|(
literal|101
argument_list|)
block|,
comment|/**      * The request has succeeded. The information returned with the response is dependent on the method      * used in the request, for example:      *<ul>      *<li>GET: an entity corresponding to the requested resource is sent in the response;</li>      *<li>HEAD: the entity-header fields corresponding to the requested resource are sent in the response without any message-body;</li>      *<li>POST: an entity describing or containing the result of the action;</li>      *<li>TRACE: an entity containing the request message as received by the end server.</li>      *</ul>      */
DECL|enum constant|OK
name|OK
argument_list|(
literal|200
argument_list|)
block|,
comment|/**      * The request has been fulfilled and resulted in a new resource being created. The newly created resource can      * be referenced by the URI(s) returned in the entity of the response, with the most specific URI for the      * resource given by a Location header field. The response SHOULD include an entity containing a list of resource      * characteristics and location(s) from which the user or user agent can choose the one most appropriate. The      * entity format is specified by the media type given in the Content-Type header field. The origin server MUST      * create the resource before returning the 201 status code. If the action cannot be carried out immediately, the      * server SHOULD respond with 202 (Accepted) response instead.      *<p>      * A 201 response MAY contain an ETag response header field indicating the current value of the entity tag      * for the requested variant just created, see section 14.19.      */
DECL|enum constant|CREATED
name|CREATED
argument_list|(
literal|201
argument_list|)
block|,
comment|/**      * The request has been accepted for processing, but the processing has not been completed.  The request might      * or might not eventually be acted upon, as it might be disallowed when processing actually takes place. There      * is no facility for re-sending a status code from an asynchronous operation such as this.      *<p>      * The 202 response is intentionally non-committal. Its purpose is to allow a server to accept a request for      * some other process (perhaps a batch-oriented process that is only run once per day) without requiring that      * the user agent's connection to the server persist until the process is completed. The entity returned with      * this response SHOULD include an indication of the request's current status and either a pointer to a status      * monitor or some estimate of when the user can expect the request to be fulfilled.      */
DECL|enum constant|ACCEPTED
name|ACCEPTED
argument_list|(
literal|202
argument_list|)
block|,
comment|/**      * The returned meta information in the entity-header is not the definitive set as available from the origin      * server, but is gathered from a local or a third-party copy. The set presented MAY be a subset or super set      * of the original version. For example, including local annotation information about the resource might      * result in a super set of the meta information known by the origin server. Use of this response code      * is not required and is only appropriate when the response would otherwise be 200 (OK).      */
DECL|enum constant|NON_AUTHORITATIVE_INFORMATION
name|NON_AUTHORITATIVE_INFORMATION
argument_list|(
literal|203
argument_list|)
block|,
comment|/**      * The server has fulfilled the request but does not need to return an entity-body, and might want to return      * updated meta information. The response MAY include new or updated meta information in the form of      * entity-headers, which if present SHOULD be associated with the requested variant.      *<p>      * If the client is a user agent, it SHOULD NOT change its document view from that which caused the request      * to be sent. This response is primarily intended to allow input for actions to take place without causing a      * change to the user agent's active document view, although any new or updated meta information SHOULD be      * applied to the document currently in the user agent's active view.      *<p>      * The 204 response MUST NOT include a message-body, and thus is always terminated by the first empty      * line after the header fields.      */
DECL|enum constant|NO_CONTENT
name|NO_CONTENT
argument_list|(
literal|204
argument_list|)
block|,
comment|/**      * The server has fulfilled the request and the user agent SHOULD reset the document view which caused the      * request to be sent. This response is primarily intended to allow input for actions to take place via user      * input, followed by a clearing of the form in which the input is given so that the user can easily initiate      * another input action. The response MUST NOT include an entity.      */
DECL|enum constant|RESET_CONTENT
name|RESET_CONTENT
argument_list|(
literal|205
argument_list|)
block|,
comment|/**      * The server has fulfilled the partial GET request for the resource. The request MUST have included a Range      * header field (section 14.35) indicating the desired range, and MAY have included an If-Range header      * field (section 14.27) to make the request conditional.      *<p>      * The response MUST include the following header fields:      *<ul>      *<li>Either a Content-Range header field (section 14.16) indicating the range included with this response,      * or a multipart/byteranges Content-Type including Content-Range fields for each part. If a Content-Length      * header field is present in the response, its value MUST match the actual number of OCTETs transmitted in      * the message-body.</li>      *<li>Date</li>      *<li>ETag and/or Content-Location, if the header would have been sent in a 200 response to the same request</li>      *<li>Expires, Cache-Control, and/or Vary, if the field-value might differ from that sent in any previous      * response for the same variant</li>      *</ul>      *<p>      * If the 206 response is the result of an If-Range request that used a strong cache validator      * (see section 13.3.3), the response SHOULD NOT include other entity-headers. If the response is the result      * of an If-Range request that used a weak validator, the response MUST NOT include other entity-headers;      * this prevents inconsistencies between cached entity-bodies and updated headers. Otherwise, the response MUST      * include all of the entity-headers that would have been returned with a 200 (OK) response to the same request.      *<p>      * A cache MUST NOT combine a 206 response with other previously cached content if the ETag or Last-Modified      * headers do not match exactly, see 13.5.4.      *<p>      * A cache that does not support the Range and Content-Range headers MUST NOT cache 206 (Partial) responses.      */
DECL|enum constant|PARTIAL_CONTENT
name|PARTIAL_CONTENT
argument_list|(
literal|206
argument_list|)
block|,
comment|/**      * The 207 (Multi-Status) status code provides status for multiple independent operations (see Section 13 for      * more information).      *<p>      * A Multi-Status response conveys information about multiple resources in situations where multiple status      * codes might be appropriate. The default Multi-Status response body is a text/xml or application/xml HTTP      * entity with a 'multistatus' root element. Further elements contain 200, 300, 400, and 500 series status codes      * generated during the method invocation. 100 series status codes SHOULD NOT be recorded in a 'response'      * XML element.      *<p>      * Although '207' is used as the overall response status code, the recipient needs to consult the contents      * of the multistatus response body for further information about the success or failure of the method execution.      * The response MAY be used in success, partial success and also in failure situations.      *<p>      * The 'multistatus' root element holds zero or more 'response' elements in any order, each with      * information about an individual resource. Each 'response' element MUST have an 'href' element      * to identify the resource.      */
DECL|enum constant|MULTI_STATUS
name|MULTI_STATUS
argument_list|(
literal|207
argument_list|)
block|,
comment|/**      * The requested resource corresponds to any one of a set of representations, each with its own specific      * location, and agent-driven negotiation information (section 12) is being provided so that the user (or user      * agent) can select a preferred representation and redirect its request to that location.      *<p>      * Unless it was a HEAD request, the response SHOULD include an entity containing a list of resource      * characteristics and location(s) from which the user or user agent can choose the one most appropriate.      * The entity format is specified by the media type given in the Content-Type header field. Depending upon the      * format and the capabilities of the user agent, selection of the most appropriate choice MAY be performed      * automatically. However, this specification does not define any standard for such automatic selection.      *<p>      * If the server has a preferred choice of representation, it SHOULD include the specific URI for that      * representation in the Location field; user agents MAY use the Location field value for automatic redirection.      * This response is cacheable unless indicated otherwise.      */
DECL|enum constant|MULTIPLE_CHOICES
name|MULTIPLE_CHOICES
argument_list|(
literal|300
argument_list|)
block|,
comment|/**      * The requested resource has been assigned a new permanent URI and any future references to this resource      * SHOULD use one of the returned URIs.  Clients with link editing capabilities ought to automatically re-link      * references to the Request-URI to one or more of the new references returned by the server, where possible.      * This response is cacheable unless indicated otherwise.      *<p>      * The new permanent URI SHOULD be given by the Location field in the response. Unless the request method      * was HEAD, the entity of the response SHOULD contain a short hypertext note with a hyperlink to the new URI(s).      *<p>      * If the 301 status code is received in response to a request other than GET or HEAD, the user agent      * MUST NOT automatically redirect the request unless it can be confirmed by the user, since this might change      * the conditions under which the request was issued.      */
DECL|enum constant|MOVED_PERMANENTLY
name|MOVED_PERMANENTLY
argument_list|(
literal|301
argument_list|)
block|,
comment|/**      * The requested resource resides temporarily under a different URI. Since the redirection might be altered on      * occasion, the client SHOULD continue to use the Request-URI for future requests.  This response is only      * cacheable if indicated by a Cache-Control or Expires header field.      *<p>      * The temporary URI SHOULD be given by the Location field in the response. Unless the request method was      * HEAD, the entity of the response SHOULD contain a short hypertext note with a hyperlink to the new URI(s).      *<p>      * If the 302 status code is received in response to a request other than GET or HEAD, the user agent      * MUST NOT automatically redirect the request unless it can be confirmed by the user, since this might change      * the conditions under which the request was issued.      */
DECL|enum constant|FOUND
name|FOUND
argument_list|(
literal|302
argument_list|)
block|,
comment|/**      * The response to the request can be found under a different URI and SHOULD be retrieved using a GET method on      * that resource. This method exists primarily to allow the output of a POST-activated script to redirect the      * user agent to a selected resource. The new URI is not a substitute reference for the originally requested      * resource. The 303 response MUST NOT be cached, but the response to the second (redirected) request might be      * cacheable.      *<p>      * The different URI SHOULD be given by the Location field in the response. Unless the request method was      * HEAD, the entity of the response SHOULD contain a short hypertext note with a hyperlink to the new URI(s).      */
DECL|enum constant|SEE_OTHER
name|SEE_OTHER
argument_list|(
literal|303
argument_list|)
block|,
comment|/**      * If the client has performed a conditional GET request and access is allowed, but the document has not been      * modified, the server SHOULD respond with this status code. The 304 response MUST NOT contain a message-body,      * and thus is always terminated by the first empty line after the header fields.      *<p>      * The response MUST include the following header fields:      *<ul>      *<li>Date, unless its omission is required by section 14.18.1      * If a clockless origin server obeys these rules, and proxies and clients add their own Date to any      * response received without one (as already specified by [RFC 2068], section 14.19), caches will operate      * correctly.      *</li>      *<li>ETag and/or Content-Location, if the header would have been sent in a 200 response to the same request</li>      *<li>Expires, Cache-Control, and/or Vary, if the field-value might differ from that sent in any previous      * response for the same variant</li>      *</ul>      *<p>      * If the conditional GET used a strong cache validator (see section 13.3.3), the response SHOULD NOT include      * other entity-headers. Otherwise (i.e., the conditional GET used a weak validator), the response MUST NOT      * include other entity-headers; this prevents inconsistencies between cached entity-bodies and updated headers.      *<p>      * If a 304 response indicates an entity not currently cached, then the cache MUST disregard the response      * and repeat the request without the conditional.      *<p>      * If a cache uses a received 304 response to update a cache entry, the cache MUST update the entry to      * reflect any new field values given in the response.      */
DECL|enum constant|NOT_MODIFIED
name|NOT_MODIFIED
argument_list|(
literal|304
argument_list|)
block|,
comment|/**      * The requested resource MUST be accessed through the proxy given by the Location field. The Location field      * gives the URI of the proxy. The recipient is expected to repeat this single request via the proxy.      * 305 responses MUST only be generated by origin servers.      */
DECL|enum constant|USE_PROXY
name|USE_PROXY
argument_list|(
literal|305
argument_list|)
block|,
comment|/**      * The requested resource resides temporarily under a different URI. Since the redirection MAY be altered on      * occasion, the client SHOULD continue to use the Request-URI for future requests.  This response is only      * cacheable if indicated by a Cache-Control or Expires header field.      *<p>      * The temporary URI SHOULD be given by the Location field in the response. Unless the request method was      * HEAD, the entity of the response SHOULD contain a short hypertext note with a hyperlink to the new URI(s) ,      * since many pre-HTTP/1.1 user agents do not understand the 307 status. Therefore, the note SHOULD contain      * the information necessary for a user to repeat the original request on the new URI.      *<p>      * If the 307 status code is received in response to a request other than GET or HEAD, the user agent MUST NOT      * automatically redirect the request unless it can be confirmed by the user, since this might change the      * conditions under which the request was issued.      */
DECL|enum constant|TEMPORARY_REDIRECT
name|TEMPORARY_REDIRECT
argument_list|(
literal|307
argument_list|)
block|,
comment|/**      * The request could not be understood by the server due to malformed syntax. The client SHOULD NOT repeat the      * request without modifications.      */
DECL|enum constant|BAD_REQUEST
name|BAD_REQUEST
argument_list|(
literal|400
argument_list|)
block|,
comment|/**      * The request requires user authentication. The response MUST include a WWW-Authenticate header field      * (section 14.47) containing a challenge applicable to the requested resource. The client MAY repeat the request      * with a suitable Authorization header field (section 14.8). If the request already included Authorization      * credentials, then the 401 response indicates that authorization has been refused for those credentials.      * If the 401 response contains the same challenge as the prior response, and the user agent has already attempted      * authentication at least once, then the user SHOULD be presented the entity that was given in the response,      * since that entity might include relevant diagnostic information. HTTP access authentication is explained in      * "HTTP Authentication: Basic and Digest Access Authentication" [43].      */
DECL|enum constant|UNAUTHORIZED
name|UNAUTHORIZED
argument_list|(
literal|401
argument_list|)
block|,
comment|/**      * This code is reserved for future use.      */
DECL|enum constant|PAYMENT_REQUIRED
name|PAYMENT_REQUIRED
argument_list|(
literal|402
argument_list|)
block|,
comment|/**      * The server understood the request, but is refusing to fulfill it. Authorization will not help and the request      * SHOULD NOT be repeated. If the request method was not HEAD and the server wishes to make public why the      * request has not been fulfilled, it SHOULD describe the reason for the refusal in the entity.  If the server      * does not wish to make this information available to the client, the status code 404 (Not Found) can be used      * instead.      */
DECL|enum constant|FORBIDDEN
name|FORBIDDEN
argument_list|(
literal|403
argument_list|)
block|,
comment|/**      * The server has not found anything matching the Request-URI. No indication is given of whether the condition      * is temporary or permanent. The 410 (Gone) status code SHOULD be used if the server knows, through some      * internally configurable mechanism, that an old resource is permanently unavailable and has no forwarding      * address. This status code is commonly used when the server does not wish to reveal exactly why the request      * has been refused, or when no other response is applicable.      */
DECL|enum constant|NOT_FOUND
name|NOT_FOUND
argument_list|(
literal|404
argument_list|)
block|,
comment|/**      * The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.      * The response MUST include an Allow header containing a list of valid methods for the requested resource.      */
DECL|enum constant|METHOD_NOT_ALLOWED
name|METHOD_NOT_ALLOWED
argument_list|(
literal|405
argument_list|)
block|,
comment|/**      * The resource identified by the request is only capable of generating response entities which have content      * characteristics not acceptable according to the accept headers sent in the request.      *<p>      * Unless it was a HEAD request, the response SHOULD include an entity containing a list of available entity      * characteristics and location(s) from which the user or user agent can choose the one most appropriate.      * The entity format is specified by the media type given in the Content-Type header field. Depending upon the      * format and the capabilities of the user agent, selection of the most appropriate choice MAY be performed      * automatically. However, this specification does not define any standard for such automatic selection.      *<p>      * Note: HTTP/1.1 servers are allowed to return responses which are not acceptable according to the accept      * headers sent in the request. In some cases, this may even be preferable to sending a 406 response. User      * agents are encouraged to inspect the headers of an incoming response to determine if it is acceptable.      *<p>      * If the response could be unacceptable, a user agent SHOULD temporarily stop receipt of more data and query      * the user for a decision on further actions.      */
DECL|enum constant|NOT_ACCEPTABLE
name|NOT_ACCEPTABLE
argument_list|(
literal|406
argument_list|)
block|,
comment|/**      * This code is similar to 401 (Unauthorized), but indicates that the client must first authenticate itself with      * the proxy. The proxy MUST return a Proxy-Authenticate header field (section 14.33) containing a challenge      * applicable to the proxy for the requested resource. The client MAY repeat the request with a suitable      * Proxy-Authorization header field (section 14.34). HTTP access authentication is explained in      * "HTTP Authentication: Basic and Digest Access Authentication" [43].      */
DECL|enum constant|PROXY_AUTHENTICATION
name|PROXY_AUTHENTICATION
argument_list|(
literal|407
argument_list|)
block|,
comment|/**      * The client did not produce a request within the time that the server was prepared to wait. The client MAY      * repeat the request without modifications at any later time.      */
DECL|enum constant|REQUEST_TIMEOUT
name|REQUEST_TIMEOUT
argument_list|(
literal|408
argument_list|)
block|,
comment|/**      * The request could not be completed due to a conflict with the current state of the resource. This code is      * only allowed in situations where it is expected that the user might be able to resolve the conflict and      * resubmit the request. The response body SHOULD include enough information for the user to recognize the      * source of the conflict. Ideally, the response entity would include enough information for the user or user      * agent to fix the problem; however, that might not be possible and is not required.      *<p>      * Conflicts are most likely to occur in response to a PUT request. For example, if versioning were being      * used and the entity being PUT included changes to a resource which conflict with those made by an earlier      * (third-party) request, the server might use the 409 response to indicate that it can't complete the request.      * In this case, the response entity would likely contain a list of the differences between the two versions in      * a format defined by the response Content-Type.      */
DECL|enum constant|CONFLICT
name|CONFLICT
argument_list|(
literal|409
argument_list|)
block|,
comment|/**      * The requested resource is no longer available at the server and no forwarding address is known. This condition      * is expected to be considered permanent. Clients with link editing capabilities SHOULD delete references to      * the Request-URI after user approval. If the server does not know, or has no facility to determine, whether or      * not the condition is permanent, the status code 404 (Not Found) SHOULD be used instead. This response is      * cacheable unless indicated otherwise.      *<p>      * The 410 response is primarily intended to assist the task of web maintenance by notifying the recipient      * that the resource is intentionally unavailable and that the server owners desire that remote links to that      * resource be removed. Such an event is common for limited-time, promotional services and for resources belonging      * to individuals no longer working at the server's site. It is not necessary to mark all permanently unavailable      * resources as "gone" or to keep the mark for any length of time -- that is left to the discretion of the server      * owner.      */
DECL|enum constant|GONE
name|GONE
argument_list|(
literal|410
argument_list|)
block|,
comment|/**      * The server refuses to accept the request without a defined Content-Length. The client MAY repeat the request      * if it adds a valid Content-Length header field containing the length of the message-body in the request message.      */
DECL|enum constant|LENGTH_REQUIRED
name|LENGTH_REQUIRED
argument_list|(
literal|411
argument_list|)
block|,
comment|/**      * The precondition given in one or more of the request-header fields evaluated to false when it was tested on      * the server. This response code allows the client to place preconditions on the current resource metainformation      * (header field data) and thus prevent the requested method from being applied to a resource other than the one      * intended.      */
DECL|enum constant|PRECONDITION_FAILED
name|PRECONDITION_FAILED
argument_list|(
literal|412
argument_list|)
block|,
comment|/**      * The server is refusing to process a request because the request entity is larger than the server is willing      * or able to process. The server MAY close the connection to prevent the client from continuing the request.      *<p>      * If the condition is temporary, the server SHOULD include a Retry-After header field to indicate that it      * is temporary and after what time the client MAY try again.      */
DECL|enum constant|REQUEST_ENTITY_TOO_LARGE
name|REQUEST_ENTITY_TOO_LARGE
argument_list|(
literal|413
argument_list|)
block|,
comment|/**      * The server is refusing to service the request because the Request-URI is longer than the server is willing      * to interpret. This rare condition is only likely to occur when a client has improperly converted a POST      * request to a GET request with long query information, when the client has descended into a URI "black hole"      * of redirection (e.g., a redirected URI prefix that points to a suffix of itself), or when the server is      * under attack by a client attempting to exploit security holes present in some servers using fixed-length      * buffers for reading or manipulating the Request-URI.      */
DECL|enum constant|REQUEST_URI_TOO_LONG
name|REQUEST_URI_TOO_LONG
argument_list|(
literal|414
argument_list|)
block|,
comment|/**      * The server is refusing to service the request because the entity of the request is in a format not supported      * by the requested resource for the requested method.      */
DECL|enum constant|UNSUPPORTED_MEDIA_TYPE
name|UNSUPPORTED_MEDIA_TYPE
argument_list|(
literal|415
argument_list|)
block|,
comment|/**      * A server SHOULD return a response with this status code if a request included a Range request-header field      * (section 14.35), and none of the range-specifier values in this field overlap the current extent of the      * selected resource, and the request did not include an If-Range request-header field. (For byte-ranges, this      * means that the first-byte-pos of all of the byte-range-spec values were greater than the current length of      * the selected resource.)      *<p>      * When this status code is returned for a byte-range request, the response SHOULD include a Content-Range      * entity-header field specifying the current length of the selected resource (see section 14.16). This      * response MUST NOT use the multipart/byteranges content-type.      */
DECL|enum constant|REQUESTED_RANGE_NOT_SATISFIED
name|REQUESTED_RANGE_NOT_SATISFIED
argument_list|(
literal|416
argument_list|)
block|,
comment|/**      * The expectation given in an Expect request-header field (see section 14.20) could not be met by this server,      * or, if the server is a proxy, the server has unambiguous evidence that the request could not be met by the      * next-hop server.      */
DECL|enum constant|EXPECTATION_FAILED
name|EXPECTATION_FAILED
argument_list|(
literal|417
argument_list|)
block|,
comment|/**      * The 422 (Unprocessable Entity) status code means the server understands the content type of the request      * entity (hence a 415(Unsupported Media Type) status code is inappropriate), and the syntax of the request      * entity is correct (thus a 400 (Bad Request) status code is inappropriate) but was unable to process the      * contained instructions. For example, this error condition may occur if an XML request body contains      * well-formed (i.e., syntactically correct), but semantically erroneous, XML instructions.      */
DECL|enum constant|UNPROCESSABLE_ENTITY
name|UNPROCESSABLE_ENTITY
argument_list|(
literal|422
argument_list|)
block|,
comment|/**      * The 423 (Locked) status code means the source or destination resource of a method is locked. This response      * SHOULD contain an appropriate precondition or postcondition code, such as 'lock-token-submitted' or      * 'no-conflicting-lock'.      */
DECL|enum constant|LOCKED
name|LOCKED
argument_list|(
literal|423
argument_list|)
block|,
comment|/**      * The 424 (Failed Dependency) status code means that the method could not be performed on the resource because      * the requested action depended on another action and that action failed. For example, if a command in a      * PROPPATCH method fails, then, at minimum, the rest of the commands will also fail with 424 (Failed Dependency).      */
DECL|enum constant|FAILED_DEPENDENCY
name|FAILED_DEPENDENCY
argument_list|(
literal|424
argument_list|)
block|,
comment|/**      * 429 Too Many Requests (RFC6585)      */
DECL|enum constant|TOO_MANY_REQUESTS
name|TOO_MANY_REQUESTS
argument_list|(
literal|429
argument_list|)
block|,
comment|/**      * The server encountered an unexpected condition which prevented it from fulfilling the request.      */
DECL|enum constant|INTERNAL_SERVER_ERROR
name|INTERNAL_SERVER_ERROR
argument_list|(
literal|500
argument_list|)
block|,
comment|/**      * The server does not support the functionality required to fulfill the request. This is the appropriate      * response when the server does not recognize the request method and is not capable of supporting it for any      * resource.      */
DECL|enum constant|NOT_IMPLEMENTED
name|NOT_IMPLEMENTED
argument_list|(
literal|501
argument_list|)
block|,
comment|/**      * The server, while acting as a gateway or proxy, received an invalid response from the upstream server it      * accessed in attempting to fulfill the request.      */
DECL|enum constant|BAD_GATEWAY
name|BAD_GATEWAY
argument_list|(
literal|502
argument_list|)
block|,
comment|/**      * The server is currently unable to handle the request due to a temporary overloading or maintenance of the      * server. The implication is that this is a temporary condition which will be alleviated after some delay.      * If known, the length of the delay MAY be indicated in a Retry-After header. If no Retry-After is given,      * the client SHOULD handle the response as it would for a 500 response.      */
DECL|enum constant|SERVICE_UNAVAILABLE
name|SERVICE_UNAVAILABLE
argument_list|(
literal|503
argument_list|)
block|,
comment|/**      * The server, while acting as a gateway or proxy, did not receive a timely response from the upstream server      * specified by the URI (e.g. HTTP, FTP, LDAP) or some other auxiliary server (e.g. DNS) it needed to access      * in attempting to complete the request.      */
DECL|enum constant|GATEWAY_TIMEOUT
name|GATEWAY_TIMEOUT
argument_list|(
literal|504
argument_list|)
block|,
comment|/**      * The server does not support, or refuses to support, the HTTP protocol version that was used in the request      * message. The server is indicating that it is unable or unwilling to complete the request using the same major      * version as the client, as described in section 3.1, other than with this error message. The response SHOULD      * contain an entity describing why that version is not supported and what other protocols are supported by      * that server.      */
DECL|enum constant|HTTP_VERSION_NOT_SUPPORTED
name|HTTP_VERSION_NOT_SUPPORTED
argument_list|(
literal|505
argument_list|)
block|,
comment|/**      * The 507 (Insufficient Storage) status code means the method could not be performed on the resource because      * the server is unable to store the representation needed to successfully complete the request. This condition      * is considered to be temporary. If the request that received this status code was the result of a user action,      * the request MUST NOT be repeated until it is requested by a separate user action.      */
DECL|enum constant|INSUFFICIENT_STORAGE
name|INSUFFICIENT_STORAGE
argument_list|(
literal|506
argument_list|)
block|;
DECL|field|CODE_TO_STATUS
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|RestStatus
argument_list|>
name|CODE_TO_STATUS
decl_stmt|;
static|static
block|{
name|RestStatus
index|[]
name|values
init|=
name|values
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|RestStatus
argument_list|>
name|codeToStatus
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|values
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|RestStatus
name|value
range|:
name|values
control|)
block|{
name|codeToStatus
operator|.
name|put
argument_list|(
name|value
operator|.
name|status
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|CODE_TO_STATUS
operator|=
name|unmodifiableMap
argument_list|(
name|codeToStatus
argument_list|)
expr_stmt|;
block|}
DECL|field|status
specifier|private
name|int
name|status
decl_stmt|;
DECL|method|RestStatus
name|RestStatus
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
operator|(
name|short
operator|)
name|status
expr_stmt|;
block|}
DECL|method|getStatus
specifier|public
name|int
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|RestStatus
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|RestStatus
operator|.
name|valueOf
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
return|;
block|}
DECL|method|writeTo
specifier|public
specifier|static
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|RestStatus
name|status
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|status
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|status
specifier|public
specifier|static
name|RestStatus
name|status
parameter_list|(
name|int
name|successfulShards
parameter_list|,
name|int
name|totalShards
parameter_list|,
name|ShardOperationFailedException
modifier|...
name|failures
parameter_list|)
block|{
if|if
condition|(
name|failures
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|successfulShards
operator|==
literal|0
operator|&&
name|totalShards
operator|>
literal|0
condition|)
block|{
return|return
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
return|;
block|}
return|return
name|RestStatus
operator|.
name|OK
return|;
block|}
name|RestStatus
name|status
init|=
name|RestStatus
operator|.
name|OK
decl_stmt|;
if|if
condition|(
name|successfulShards
operator|==
literal|0
operator|&&
name|totalShards
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|ShardOperationFailedException
name|failure
range|:
name|failures
control|)
block|{
name|RestStatus
name|shardStatus
init|=
name|failure
operator|.
name|status
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardStatus
operator|.
name|getStatus
argument_list|()
operator|>=
name|status
operator|.
name|getStatus
argument_list|()
condition|)
block|{
name|status
operator|=
name|failure
operator|.
name|status
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|status
return|;
block|}
return|return
name|status
return|;
block|}
comment|/**      * Turn a status code into a {@link RestStatus}, returning null if we don't know that status.      */
DECL|method|fromCode
specifier|public
specifier|static
name|RestStatus
name|fromCode
parameter_list|(
name|int
name|code
parameter_list|)
block|{
return|return
name|CODE_TO_STATUS
operator|.
name|get
argument_list|(
name|code
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

