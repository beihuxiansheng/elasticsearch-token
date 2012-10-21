begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch
package|package
name|org
operator|.
name|elasticsearch
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
name|Nullable
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
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|Version
specifier|public
class|class
name|Version
implements|implements
name|Serializable
block|{
comment|// The logic for ID is: XXYYZZAA, where XX is major version, YY is minor version, ZZ is revision, and AA is Beta/RC indicator
comment|// AA values below 50 are beta builds, and below 99 are RC builds, with 99 indicating a release
comment|// the (internal) format of the id is there so we can easily do after/before checks on the id
DECL|field|V_0_18_0_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_18_0_ID
init|=
comment|/*00*/
literal|180099
decl_stmt|;
DECL|field|V_0_18_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_18_0
init|=
operator|new
name|Version
argument_list|(
name|V_0_18_0_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_18_1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_18_1_ID
init|=
comment|/*00*/
literal|180199
decl_stmt|;
DECL|field|V_0_18_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_18_1
init|=
operator|new
name|Version
argument_list|(
name|V_0_18_1_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_18_2_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_18_2_ID
init|=
comment|/*00*/
literal|180299
decl_stmt|;
DECL|field|V_0_18_2
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_18_2
init|=
operator|new
name|Version
argument_list|(
name|V_0_18_2_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_18_3_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_18_3_ID
init|=
comment|/*00*/
literal|180399
decl_stmt|;
DECL|field|V_0_18_3
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_18_3
init|=
operator|new
name|Version
argument_list|(
name|V_0_18_3_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_18_4_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_18_4_ID
init|=
comment|/*00*/
literal|180499
decl_stmt|;
DECL|field|V_0_18_4
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_18_4
init|=
operator|new
name|Version
argument_list|(
name|V_0_18_4_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_18_5_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_18_5_ID
init|=
comment|/*00*/
literal|180599
decl_stmt|;
DECL|field|V_0_18_5
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_18_5
init|=
operator|new
name|Version
argument_list|(
name|V_0_18_5_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_18_6_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_18_6_ID
init|=
comment|/*00*/
literal|180699
decl_stmt|;
DECL|field|V_0_18_6
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_18_6
init|=
operator|new
name|Version
argument_list|(
name|V_0_18_6_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_18_7_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_18_7_ID
init|=
comment|/*00*/
literal|180799
decl_stmt|;
DECL|field|V_0_18_7
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_18_7
init|=
operator|new
name|Version
argument_list|(
name|V_0_18_7_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_18_8_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_18_8_ID
init|=
comment|/*00*/
literal|180899
decl_stmt|;
DECL|field|V_0_18_8
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_18_8
init|=
operator|new
name|Version
argument_list|(
name|V_0_18_8_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_0_RC1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_0_RC1_ID
init|=
comment|/*00*/
literal|190051
decl_stmt|;
DECL|field|V_0_19_0_RC1
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_0_RC1
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_0_RC1_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_0_RC2_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_0_RC2_ID
init|=
comment|/*00*/
literal|190052
decl_stmt|;
DECL|field|V_0_19_0_RC2
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_0_RC2
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_0_RC2_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_0_RC3_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_0_RC3_ID
init|=
comment|/*00*/
literal|190053
decl_stmt|;
DECL|field|V_0_19_0_RC3
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_0_RC3
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_0_RC3_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_0_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_0_ID
init|=
comment|/*00*/
literal|190099
decl_stmt|;
DECL|field|V_0_19_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_0
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_0_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_1_ID
init|=
comment|/*00*/
literal|190199
decl_stmt|;
DECL|field|V_0_19_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_1
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_1_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_2_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_2_ID
init|=
comment|/*00*/
literal|190299
decl_stmt|;
DECL|field|V_0_19_2
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_2
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_2_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_3_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_3_ID
init|=
comment|/*00*/
literal|190399
decl_stmt|;
DECL|field|V_0_19_3
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_3
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_3_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_4_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_4_ID
init|=
comment|/*00*/
literal|190499
decl_stmt|;
DECL|field|V_0_19_4
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_4
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_4_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_5_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_5_ID
init|=
comment|/*00*/
literal|190599
decl_stmt|;
DECL|field|V_0_19_5
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_5
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_5_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_6_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_6_ID
init|=
comment|/*00*/
literal|190699
decl_stmt|;
DECL|field|V_0_19_6
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_6
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_6_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_7_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_7_ID
init|=
comment|/*00*/
literal|190799
decl_stmt|;
DECL|field|V_0_19_7
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_7
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_7_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_8_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_8_ID
init|=
comment|/*00*/
literal|190899
decl_stmt|;
DECL|field|V_0_19_8
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_8
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_8_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_9_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_9_ID
init|=
comment|/*00*/
literal|190999
decl_stmt|;
DECL|field|V_0_19_9
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_9
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_9_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_19_10_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_19_10_ID
init|=
comment|/*00*/
literal|191099
decl_stmt|;
DECL|field|V_0_19_10
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_19_10
init|=
operator|new
name|Version
argument_list|(
name|V_0_19_10_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_20_0_RC1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_20_0_RC1_ID
init|=
comment|/*00*/
literal|200051
decl_stmt|;
DECL|field|V_0_20_0_RC1
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_20_0_RC1
init|=
operator|new
name|Version
argument_list|(
name|V_0_20_0_RC1_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|V_0_21_0_Beta1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_0_21_0_Beta1_ID
init|=
comment|/*00*/
literal|210001
decl_stmt|;
DECL|field|V_0_21_0_Beta1
specifier|public
specifier|static
specifier|final
name|Version
name|V_0_21_0_Beta1
init|=
operator|new
name|Version
argument_list|(
name|V_0_21_0_Beta1_ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
DECL|field|CURRENT
specifier|public
specifier|static
specifier|final
name|Version
name|CURRENT
init|=
name|V_0_21_0_Beta1
decl_stmt|;
DECL|method|readVersion
specifier|public
specifier|static
name|Version
name|readVersion
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fromId
argument_list|(
name|in
operator|.
name|readVInt
argument_list|()
argument_list|)
return|;
block|}
DECL|method|fromId
specifier|public
specifier|static
name|Version
name|fromId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
switch|switch
condition|(
name|id
condition|)
block|{
case|case
name|V_0_21_0_Beta1_ID
case|:
return|return
name|V_0_21_0_Beta1
return|;
case|case
name|V_0_20_0_RC1_ID
case|:
return|return
name|V_0_20_0_RC1
return|;
case|case
name|V_0_19_0_RC1_ID
case|:
return|return
name|V_0_19_0_RC1
return|;
case|case
name|V_0_19_0_RC2_ID
case|:
return|return
name|V_0_19_0_RC2
return|;
case|case
name|V_0_19_0_RC3_ID
case|:
return|return
name|V_0_19_0_RC3
return|;
case|case
name|V_0_19_0_ID
case|:
return|return
name|V_0_19_0
return|;
case|case
name|V_0_19_1_ID
case|:
return|return
name|V_0_19_1
return|;
case|case
name|V_0_19_2_ID
case|:
return|return
name|V_0_19_2
return|;
case|case
name|V_0_19_3_ID
case|:
return|return
name|V_0_19_3
return|;
case|case
name|V_0_19_4_ID
case|:
return|return
name|V_0_19_4
return|;
case|case
name|V_0_19_5_ID
case|:
return|return
name|V_0_19_5
return|;
case|case
name|V_0_19_6_ID
case|:
return|return
name|V_0_19_6
return|;
case|case
name|V_0_19_7_ID
case|:
return|return
name|V_0_19_7
return|;
case|case
name|V_0_19_8_ID
case|:
return|return
name|V_0_19_8
return|;
case|case
name|V_0_19_9_ID
case|:
return|return
name|V_0_19_9
return|;
case|case
name|V_0_19_10_ID
case|:
return|return
name|V_0_19_10
return|;
case|case
name|V_0_18_0_ID
case|:
return|return
name|V_0_18_0
return|;
case|case
name|V_0_18_1_ID
case|:
return|return
name|V_0_18_1
return|;
case|case
name|V_0_18_2_ID
case|:
return|return
name|V_0_18_2
return|;
case|case
name|V_0_18_3_ID
case|:
return|return
name|V_0_18_3
return|;
case|case
name|V_0_18_4_ID
case|:
return|return
name|V_0_18_4
return|;
case|case
name|V_0_18_5_ID
case|:
return|return
name|V_0_18_5
return|;
case|case
name|V_0_18_6_ID
case|:
return|return
name|V_0_18_6
return|;
case|case
name|V_0_18_7_ID
case|:
return|return
name|V_0_18_7
return|;
case|case
name|V_0_18_8_ID
case|:
return|return
name|V_0_18_8
return|;
default|default:
return|return
operator|new
name|Version
argument_list|(
name|id
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
DECL|method|writeVersion
specifier|public
specifier|static
name|void
name|writeVersion
parameter_list|(
name|Version
name|version
parameter_list|,
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
name|version
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
DECL|field|id
specifier|public
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|major
specifier|public
specifier|final
name|byte
name|major
decl_stmt|;
DECL|field|minor
specifier|public
specifier|final
name|byte
name|minor
decl_stmt|;
DECL|field|revision
specifier|public
specifier|final
name|byte
name|revision
decl_stmt|;
DECL|field|build
specifier|public
specifier|final
name|byte
name|build
decl_stmt|;
DECL|field|snapshot
specifier|public
specifier|final
name|Boolean
name|snapshot
decl_stmt|;
DECL|method|Version
name|Version
parameter_list|(
name|int
name|id
parameter_list|,
annotation|@
name|Nullable
name|Boolean
name|snapshot
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|major
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|id
operator|/
literal|1000000
operator|)
operator|%
literal|100
argument_list|)
expr_stmt|;
name|this
operator|.
name|minor
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|id
operator|/
literal|10000
operator|)
operator|%
literal|100
argument_list|)
expr_stmt|;
name|this
operator|.
name|revision
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|id
operator|/
literal|100
operator|)
operator|%
literal|100
argument_list|)
expr_stmt|;
name|this
operator|.
name|build
operator|=
call|(
name|byte
call|)
argument_list|(
name|id
operator|%
literal|100
argument_list|)
expr_stmt|;
name|this
operator|.
name|snapshot
operator|=
name|snapshot
expr_stmt|;
block|}
DECL|method|snapshot
specifier|public
name|boolean
name|snapshot
parameter_list|()
block|{
return|return
name|snapshot
operator|!=
literal|null
operator|&&
name|snapshot
return|;
block|}
DECL|method|after
specifier|public
name|boolean
name|after
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
return|return
name|version
operator|.
name|id
operator|>
name|id
return|;
block|}
DECL|method|onOrAfter
specifier|public
name|boolean
name|onOrAfter
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
return|return
name|version
operator|.
name|id
operator|>=
name|id
return|;
block|}
DECL|method|before
specifier|public
name|boolean
name|before
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
return|return
name|version
operator|.
name|id
operator|<
name|id
return|;
block|}
DECL|method|onOrBefore
specifier|public
name|boolean
name|onOrBefore
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
return|return
name|version
operator|.
name|id
operator|<=
name|id
return|;
block|}
comment|/**      * Just the version number (without -SNAPSHOT if snapshot).      */
DECL|method|number
specifier|public
name|String
name|number
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|major
argument_list|)
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|minor
argument_list|)
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|revision
argument_list|)
expr_stmt|;
if|if
condition|(
name|build
operator|<
literal|50
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|".Beta"
argument_list|)
operator|.
name|append
argument_list|(
name|build
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|build
operator|<
literal|99
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|".RC"
argument_list|)
operator|.
name|append
argument_list|(
name|build
operator|-
literal|50
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ElasticSearch Version: "
operator|+
name|Version
operator|.
name|CURRENT
operator|+
literal|", JVM: "
operator|+
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|vmVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|number
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|snapshot
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"-SNAPSHOT"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Version
name|version
init|=
operator|(
name|Version
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|id
operator|!=
name|version
operator|.
name|id
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|id
return|;
block|}
block|}
end_class

end_unit

