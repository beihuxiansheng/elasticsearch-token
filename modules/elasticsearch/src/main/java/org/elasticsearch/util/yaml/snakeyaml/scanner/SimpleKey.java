begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.yaml.snakeyaml.scanner
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|scanner
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|error
operator|.
name|Mark
import|;
end_import

begin_comment
comment|/**  * Simple keys treatment.  *<p>  * Helper class for {@link ScannerImpl}.  *</p>  *  * @see ScannerImpl  */
end_comment

begin_class
DECL|class|SimpleKey
specifier|final
class|class
name|SimpleKey
block|{
DECL|field|tokenNumber
specifier|private
name|int
name|tokenNumber
decl_stmt|;
DECL|field|required
specifier|private
name|boolean
name|required
decl_stmt|;
DECL|field|index
specifier|private
name|int
name|index
decl_stmt|;
DECL|field|line
specifier|private
name|int
name|line
decl_stmt|;
DECL|field|column
specifier|private
name|int
name|column
decl_stmt|;
DECL|field|mark
specifier|private
name|Mark
name|mark
decl_stmt|;
DECL|method|SimpleKey
specifier|public
name|SimpleKey
parameter_list|(
name|int
name|tokenNumber
parameter_list|,
name|boolean
name|required
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|Mark
name|mark
parameter_list|)
block|{
name|this
operator|.
name|tokenNumber
operator|=
name|tokenNumber
expr_stmt|;
name|this
operator|.
name|required
operator|=
name|required
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|this
operator|.
name|column
operator|=
name|column
expr_stmt|;
name|this
operator|.
name|mark
operator|=
name|mark
expr_stmt|;
block|}
DECL|method|getTokenNumber
specifier|public
name|int
name|getTokenNumber
parameter_list|()
block|{
return|return
name|this
operator|.
name|tokenNumber
return|;
block|}
DECL|method|getColumn
specifier|public
name|int
name|getColumn
parameter_list|()
block|{
return|return
name|this
operator|.
name|column
return|;
block|}
DECL|method|getMark
specifier|public
name|Mark
name|getMark
parameter_list|()
block|{
return|return
name|mark
return|;
block|}
DECL|method|getIndex
specifier|public
name|int
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|getLine
specifier|public
name|int
name|getLine
parameter_list|()
block|{
return|return
name|line
return|;
block|}
DECL|method|isRequired
specifier|public
name|boolean
name|isRequired
parameter_list|()
block|{
return|return
name|required
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SimpleKey - tokenNumber="
operator|+
name|tokenNumber
operator|+
literal|" required="
operator|+
name|required
operator|+
literal|" index="
operator|+
name|index
operator|+
literal|" line="
operator|+
name|line
operator|+
literal|" column="
operator|+
name|column
return|;
block|}
block|}
end_class

end_unit

