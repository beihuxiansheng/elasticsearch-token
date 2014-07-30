begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|lookup
operator|.
name|DocLookup
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
comment|/**  * A float encapsulation that dynamically accesses the score of a document.  *  * The provided {@link DocLookup} is used to retrieve the score  * for the current document.  */
end_comment

begin_class
DECL|class|ScoreAccessor
specifier|public
specifier|final
class|class
name|ScoreAccessor
extends|extends
name|Number
block|{
DECL|field|doc
specifier|final
name|DocLookup
name|doc
decl_stmt|;
DECL|method|ScoreAccessor
specifier|public
name|ScoreAccessor
parameter_list|(
name|DocLookup
name|d
parameter_list|)
block|{
name|doc
operator|=
name|d
expr_stmt|;
block|}
DECL|method|score
name|float
name|score
parameter_list|()
block|{
try|try
block|{
return|return
name|doc
operator|.
name|score
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Could not get score"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|intValue
specifier|public
name|int
name|intValue
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|score
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|longValue
specifier|public
name|long
name|longValue
parameter_list|()
block|{
return|return
operator|(
name|long
operator|)
name|score
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|floatValue
specifier|public
name|float
name|floatValue
parameter_list|()
block|{
return|return
name|score
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doubleValue
specifier|public
name|double
name|doubleValue
parameter_list|()
block|{
return|return
name|score
argument_list|()
return|;
block|}
block|}
end_class

end_unit

