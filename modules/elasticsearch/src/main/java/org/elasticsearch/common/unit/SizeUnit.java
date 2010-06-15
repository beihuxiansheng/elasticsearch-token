begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.unit
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
package|;
end_package

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_enum
DECL|enum|SizeUnit
specifier|public
enum|enum
name|SizeUnit
block|{
DECL|enum constant|SINGLE
name|SINGLE
block|{
annotation|@
name|Override
specifier|public
name|long
name|toSingles
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toKilo
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
operator|/
operator|(
name|C1
operator|/
name|C0
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toMega
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
operator|/
operator|(
name|C2
operator|/
name|C0
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toGiga
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
operator|/
operator|(
name|C3
operator|/
name|C0
operator|)
return|;
block|}
block|}
block|,
DECL|enum constant|KILO
name|KILO
block|{
annotation|@
name|Override
specifier|public
name|long
name|toSingles
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|x
argument_list|(
name|size
argument_list|,
name|C1
operator|/
name|C0
argument_list|,
name|MAX
operator|/
operator|(
name|C1
operator|/
name|C0
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toKilo
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toMega
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
operator|/
operator|(
name|C2
operator|/
name|C1
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toGiga
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
operator|/
operator|(
name|C3
operator|/
name|C1
operator|)
return|;
block|}
block|}
block|,
DECL|enum constant|MEGA
name|MEGA
block|{
annotation|@
name|Override
specifier|public
name|long
name|toSingles
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|x
argument_list|(
name|size
argument_list|,
name|C2
operator|/
name|C0
argument_list|,
name|MAX
operator|/
operator|(
name|C2
operator|/
name|C0
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toKilo
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|x
argument_list|(
name|size
argument_list|,
name|C2
operator|/
name|C1
argument_list|,
name|MAX
operator|/
operator|(
name|C2
operator|/
name|C1
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toMega
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toGiga
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
operator|/
operator|(
name|C3
operator|/
name|C2
operator|)
return|;
block|}
block|}
block|,
DECL|enum constant|GIGA
name|GIGA
block|{
annotation|@
name|Override
specifier|public
name|long
name|toSingles
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|x
argument_list|(
name|size
argument_list|,
name|C3
operator|/
name|C0
argument_list|,
name|MAX
operator|/
operator|(
name|C3
operator|/
name|C0
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toKilo
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|x
argument_list|(
name|size
argument_list|,
name|C3
operator|/
name|C1
argument_list|,
name|MAX
operator|/
operator|(
name|C3
operator|/
name|C1
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toMega
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|x
argument_list|(
name|size
argument_list|,
name|C3
operator|/
name|C2
argument_list|,
name|MAX
operator|/
operator|(
name|C3
operator|/
name|C2
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toGiga
parameter_list|(
name|long
name|size
parameter_list|)
block|{
return|return
name|size
return|;
block|}
block|}
block|;
DECL|field|C0
specifier|static
specifier|final
name|long
name|C0
init|=
literal|1L
decl_stmt|;
DECL|field|C1
specifier|static
specifier|final
name|long
name|C1
init|=
name|C0
operator|*
literal|1000L
decl_stmt|;
DECL|field|C2
specifier|static
specifier|final
name|long
name|C2
init|=
name|C1
operator|*
literal|1000L
decl_stmt|;
DECL|field|C3
specifier|static
specifier|final
name|long
name|C3
init|=
name|C2
operator|*
literal|1000L
decl_stmt|;
DECL|field|MAX
specifier|static
specifier|final
name|long
name|MAX
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/**      * Scale d by m, checking for overflow.      * This has a short name to make above code more readable.      */
DECL|method|x
specifier|static
name|long
name|x
parameter_list|(
name|long
name|d
parameter_list|,
name|long
name|m
parameter_list|,
name|long
name|over
parameter_list|)
block|{
if|if
condition|(
name|d
operator|>
name|over
condition|)
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
if|if
condition|(
name|d
operator|<
operator|-
name|over
condition|)
return|return
name|Long
operator|.
name|MIN_VALUE
return|;
return|return
name|d
operator|*
name|m
return|;
block|}
DECL|method|toSingles
specifier|public
name|long
name|toSingles
parameter_list|(
name|long
name|size
parameter_list|)
block|{
throw|throw
operator|new
name|AbstractMethodError
argument_list|()
throw|;
block|}
DECL|method|toKilo
specifier|public
name|long
name|toKilo
parameter_list|(
name|long
name|size
parameter_list|)
block|{
throw|throw
operator|new
name|AbstractMethodError
argument_list|()
throw|;
block|}
DECL|method|toMega
specifier|public
name|long
name|toMega
parameter_list|(
name|long
name|size
parameter_list|)
block|{
throw|throw
operator|new
name|AbstractMethodError
argument_list|()
throw|;
block|}
DECL|method|toGiga
specifier|public
name|long
name|toGiga
parameter_list|(
name|long
name|size
parameter_list|)
block|{
throw|throw
operator|new
name|AbstractMethodError
argument_list|()
throw|;
block|}
block|}
end_enum

end_unit

