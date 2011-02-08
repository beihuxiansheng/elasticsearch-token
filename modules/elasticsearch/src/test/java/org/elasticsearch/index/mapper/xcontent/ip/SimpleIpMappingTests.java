begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.xcontent.ip
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|xcontent
operator|.
name|ip
package|;
end_package

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SimpleIpMappingTests
specifier|public
class|class
name|SimpleIpMappingTests
block|{
comment|// No Longer enabled...
comment|//    @Test public void testAutoIpDetection() throws Exception {
comment|//        String mapping = XContentFactory.jsonBuilder().startObject().startObject("type")
comment|//                .startObject("properties").endObject()
comment|//                .endObject().endObject().string();
comment|//
comment|//        XContentDocumentMapper defaultMapper = MapperTests.newParser().parse(mapping);
comment|//
comment|//        ParsedDocument doc = defaultMapper.parse("type", "1", XContentFactory.jsonBuilder()
comment|//                .startObject()
comment|//                .field("ip1", "127.0.0.1")
comment|//                .field("ip2", "0.1")
comment|//                .field("ip3", "127.0.0.1.2")
comment|//                .endObject()
comment|//                .copiedBytes());
comment|//
comment|//        assertThat(doc.doc().getFieldable("ip1"), notNullValue());
comment|//        assertThat(doc.doc().get("ip1"), nullValue()); // its numeric
comment|//        assertThat(doc.doc().get("ip2"), equalTo("0.1"));
comment|//        assertThat(doc.doc().get("ip3"), equalTo("127.0.0.1.2"));
comment|//    }
block|}
end_class

end_unit

