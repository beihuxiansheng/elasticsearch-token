begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
package|;
end_package

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
name|Base64
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_class
DECL|class|RandomBasedUUIDGenerator
class|class
name|RandomBasedUUIDGenerator
implements|implements
name|UUIDGenerator
block|{
comment|/**      * Returns a Base64 encoded version of a Version 4.0 compatible UUID      * as defined here: http://www.ietf.org/rfc/rfc4122.txt      */
annotation|@
name|Override
DECL|method|getBase64UUID
specifier|public
name|String
name|getBase64UUID
parameter_list|()
block|{
return|return
name|getBase64UUID
argument_list|(
name|SecureRandomHolder
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
comment|/**      * Returns a Base64 encoded version of a Version 4.0 compatible UUID      * randomly initialized by the given {@link java.util.Random} instance      * as defined here: http://www.ietf.org/rfc/rfc4122.txt      */
DECL|method|getBase64UUID
specifier|public
name|String
name|getBase64UUID
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|randomBytes
init|=
operator|new
name|byte
index|[
literal|16
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|randomBytes
argument_list|)
expr_stmt|;
comment|/* Set the version to version 4 (see http://www.ietf.org/rfc/rfc4122.txt)          * The randomly or pseudo-randomly generated version.          * The version number is in the most significant 4 bits of the time          * stamp (bits 4 through 7 of the time_hi_and_version field).*/
name|randomBytes
index|[
literal|6
index|]
operator|&=
literal|0x0f
expr_stmt|;
comment|/* clear the 4 most significant bits for the version  */
name|randomBytes
index|[
literal|6
index|]
operator||=
literal|0x40
expr_stmt|;
comment|/* set the version to 0100 / 0x40 */
comment|/* Set the variant:           * The high field of th clock sequence multiplexed with the variant.          * We set only the MSB of the variant*/
name|randomBytes
index|[
literal|8
index|]
operator|&=
literal|0x3f
expr_stmt|;
comment|/* clear the 2 most significant bits */
name|randomBytes
index|[
literal|8
index|]
operator||=
literal|0x80
expr_stmt|;
comment|/* set the variant (MSB is set)*/
return|return
name|Base64
operator|.
name|getUrlEncoder
argument_list|()
operator|.
name|withoutPadding
argument_list|()
operator|.
name|encodeToString
argument_list|(
name|randomBytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

