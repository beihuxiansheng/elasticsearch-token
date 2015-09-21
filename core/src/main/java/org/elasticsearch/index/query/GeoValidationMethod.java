begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
package|;
end_package

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
name|Writeable
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
name|util
operator|.
name|CollectionUtils
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

begin_comment
comment|/**  * This enum is used to determine how to deal with invalid geo coordinates in geo related  * queries:  *   *  On STRICT validation invalid coordinates cause an exception to be thrown.  *  On IGNORE_MALFORMED invalid coordinates are being accepted.  *  On COERCE invalid coordinates are being corrected to the most likely valid coordinate.  * */
end_comment

begin_enum
DECL|enum|GeoValidationMethod
specifier|public
enum|enum
name|GeoValidationMethod
implements|implements
name|Writeable
argument_list|<
name|GeoValidationMethod
argument_list|>
block|{
DECL|enum constant|COERCE
DECL|enum constant|IGNORE_MALFORMED
DECL|enum constant|STRICT
name|COERCE
block|,
name|IGNORE_MALFORMED
block|,
name|STRICT
block|;
DECL|field|DEFAULT
specifier|public
specifier|static
specifier|final
name|GeoValidationMethod
name|DEFAULT
init|=
name|STRICT
decl_stmt|;
DECL|field|DEFAULT_LENIENT_PARSING
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_LENIENT_PARSING
init|=
operator|(
name|DEFAULT
operator|!=
name|STRICT
operator|)
decl_stmt|;
DECL|field|PROTOTYPE
specifier|private
specifier|static
specifier|final
name|GeoValidationMethod
name|PROTOTYPE
init|=
name|DEFAULT
decl_stmt|;
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|GeoValidationMethod
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|GeoValidationMethod
operator|.
name|values
argument_list|()
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
return|;
block|}
DECL|method|readGeoValidationMethodFrom
specifier|public
specifier|static
name|GeoValidationMethod
name|readGeoValidationMethodFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|PROTOTYPE
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|this
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|GeoValidationMethod
name|fromString
parameter_list|(
name|String
name|op
parameter_list|)
block|{
for|for
control|(
name|GeoValidationMethod
name|method
range|:
name|GeoValidationMethod
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|method
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|op
argument_list|)
condition|)
block|{
return|return
name|method
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"operator needs to be either "
operator|+
name|CollectionUtils
operator|.
name|arrayAsArrayList
argument_list|(
name|GeoValidationMethod
operator|.
name|values
argument_list|()
argument_list|)
operator|+
literal|", but not ["
operator|+
name|op
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|/** Returns whether or not to skip bounding box validation. */
DECL|method|isIgnoreMalformed
specifier|public
specifier|static
name|boolean
name|isIgnoreMalformed
parameter_list|(
name|GeoValidationMethod
name|method
parameter_list|)
block|{
return|return
operator|(
name|method
operator|==
name|GeoValidationMethod
operator|.
name|IGNORE_MALFORMED
operator|||
name|method
operator|==
name|GeoValidationMethod
operator|.
name|COERCE
operator|)
return|;
block|}
comment|/** Returns whether or not to try and fix broken/wrapping bounding boxes. */
DECL|method|isCoerce
specifier|public
specifier|static
name|boolean
name|isCoerce
parameter_list|(
name|GeoValidationMethod
name|method
parameter_list|)
block|{
return|return
name|method
operator|==
name|GeoValidationMethod
operator|.
name|COERCE
return|;
block|}
comment|/** Returns validation method corresponding to given coerce and ignoreMalformed values. */
DECL|method|infer
specifier|public
specifier|static
name|GeoValidationMethod
name|infer
parameter_list|(
name|boolean
name|coerce
parameter_list|,
name|boolean
name|ignoreMalformed
parameter_list|)
block|{
if|if
condition|(
name|coerce
condition|)
block|{
return|return
name|GeoValidationMethod
operator|.
name|COERCE
return|;
block|}
elseif|else
if|if
condition|(
name|ignoreMalformed
condition|)
block|{
return|return
name|GeoValidationMethod
operator|.
name|IGNORE_MALFORMED
return|;
block|}
else|else
block|{
return|return
name|GeoValidationMethod
operator|.
name|STRICT
return|;
block|}
block|}
block|}
end_enum

end_unit

