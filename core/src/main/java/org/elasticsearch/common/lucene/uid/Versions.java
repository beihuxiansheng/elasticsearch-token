begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.uid
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|uid
package|;
end_package

begin_class
DECL|class|Versions
specifier|public
specifier|final
class|class
name|Versions
block|{
comment|/** used to indicate the write operation should succeed regardless of current version **/
DECL|field|MATCH_ANY
specifier|public
specifier|static
specifier|final
name|long
name|MATCH_ANY
init|=
operator|-
literal|3L
decl_stmt|;
comment|/** indicates that the current document was not found in lucene and in the version map */
DECL|field|NOT_FOUND
specifier|public
specifier|static
specifier|final
name|long
name|NOT_FOUND
init|=
operator|-
literal|1L
decl_stmt|;
comment|// -2 was used for docs that can be found in the index but do not have a version
comment|/**      * used to indicate that the write operation should be executed if the document is currently deleted      * i.e., not found in the index and/or found as deleted (with version) in the version map      */
DECL|field|MATCH_DELETED
specifier|public
specifier|static
specifier|final
name|long
name|MATCH_DELETED
init|=
operator|-
literal|4L
decl_stmt|;
block|}
end_class

end_unit

