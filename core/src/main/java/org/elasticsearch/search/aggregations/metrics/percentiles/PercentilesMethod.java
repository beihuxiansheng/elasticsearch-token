begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.percentiles
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|percentiles
package|;
end_package

begin_comment
comment|/**  * An enum representing the methods for calculating percentiles  */
end_comment

begin_enum
DECL|enum|PercentilesMethod
specifier|public
enum|enum
name|PercentilesMethod
block|{
comment|/**      * The TDigest method for calculating percentiles      */
DECL|enum constant|TDIGEST
name|TDIGEST
argument_list|(
literal|"tdigest"
argument_list|)
block|,
comment|/**      * The HDRHistogram method of calculating percentiles      */
DECL|enum constant|HDR
name|HDR
argument_list|(
literal|"hdr"
argument_list|)
block|;
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|method|PercentilesMethod
specifier|private
name|PercentilesMethod
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * @return the name of the method      */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Returns the {@link PercentilesMethod} for this method name. returns      *<code>null</code> if no {@link PercentilesMethod} exists for the name.      */
DECL|method|resolveFromName
specifier|public
specifier|static
name|PercentilesMethod
name|resolveFromName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|PercentilesMethod
name|method
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|method
operator|.
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|method
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_enum

end_unit

