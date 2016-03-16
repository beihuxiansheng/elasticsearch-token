begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|Strings
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
name|SuppressForbidden
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
name|inject
operator|.
name|AbstractModule
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
name|common
operator|.
name|settings
operator|.
name|Settings
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

begin_comment
comment|/**  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|class|Version
specifier|public
class|class
name|Version
block|{
comment|// The logic for ID is: XXYYZZAA, where XX is major version, YY is minor version, ZZ is revision, and AA is alpha/beta/rc indicator
comment|// AA values below 25 are for alpha builder (since 5.0), and above 25 and below 50 are beta builds, and below 99 are RC builds, with 99 indicating a release
comment|// the (internal) format of the id is there so we can easily do after/before checks on the id
DECL|field|V_2_0_0_beta1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_0_0_beta1_ID
init|=
literal|2000001
decl_stmt|;
DECL|field|V_2_0_0_beta1
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_0_0_beta1
init|=
operator|new
name|Version
argument_list|(
name|V_2_0_0_beta1_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_2_1
argument_list|)
decl_stmt|;
DECL|field|V_2_0_0_beta2_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_0_0_beta2_ID
init|=
literal|2000002
decl_stmt|;
DECL|field|V_2_0_0_beta2
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_0_0_beta2
init|=
operator|new
name|Version
argument_list|(
name|V_2_0_0_beta2_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_2_1
argument_list|)
decl_stmt|;
DECL|field|V_2_0_0_rc1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_0_0_rc1_ID
init|=
literal|2000051
decl_stmt|;
DECL|field|V_2_0_0_rc1
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_0_0_rc1
init|=
operator|new
name|Version
argument_list|(
name|V_2_0_0_rc1_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_2_1
argument_list|)
decl_stmt|;
DECL|field|V_2_0_0_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_0_0_ID
init|=
literal|2000099
decl_stmt|;
DECL|field|V_2_0_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_0_0
init|=
operator|new
name|Version
argument_list|(
name|V_2_0_0_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_2_1
argument_list|)
decl_stmt|;
DECL|field|V_2_0_1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_0_1_ID
init|=
literal|2000199
decl_stmt|;
DECL|field|V_2_0_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_0_1
init|=
operator|new
name|Version
argument_list|(
name|V_2_0_1_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_2_1
argument_list|)
decl_stmt|;
DECL|field|V_2_0_2_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_0_2_ID
init|=
literal|2000299
decl_stmt|;
DECL|field|V_2_0_2
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_0_2
init|=
operator|new
name|Version
argument_list|(
name|V_2_0_2_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_2_1
argument_list|)
decl_stmt|;
DECL|field|V_2_1_0_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_1_0_ID
init|=
literal|2010099
decl_stmt|;
DECL|field|V_2_1_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_1_0
init|=
operator|new
name|Version
argument_list|(
name|V_2_1_0_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_3_1
argument_list|)
decl_stmt|;
DECL|field|V_2_1_1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_1_1_ID
init|=
literal|2010199
decl_stmt|;
DECL|field|V_2_1_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_1_1
init|=
operator|new
name|Version
argument_list|(
name|V_2_1_1_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_3_1
argument_list|)
decl_stmt|;
DECL|field|V_2_1_2_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_1_2_ID
init|=
literal|2010299
decl_stmt|;
DECL|field|V_2_1_2
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_1_2
init|=
operator|new
name|Version
argument_list|(
name|V_2_1_2_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_3_1
argument_list|)
decl_stmt|;
DECL|field|V_2_2_0_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_2_0_ID
init|=
literal|2020099
decl_stmt|;
DECL|field|V_2_2_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_2_0
init|=
operator|new
name|Version
argument_list|(
name|V_2_2_0_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_4_1
argument_list|)
decl_stmt|;
DECL|field|V_2_2_1_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_2_1_ID
init|=
literal|2020199
decl_stmt|;
DECL|field|V_2_2_1
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_2_1
init|=
operator|new
name|Version
argument_list|(
name|V_2_2_1_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_4_1
argument_list|)
decl_stmt|;
DECL|field|V_2_3_0_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_2_3_0_ID
init|=
literal|2030099
decl_stmt|;
DECL|field|V_2_3_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_2_3_0
init|=
operator|new
name|Version
argument_list|(
name|V_2_3_0_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_5_5_0
argument_list|)
decl_stmt|;
DECL|field|V_5_0_0_ID
specifier|public
specifier|static
specifier|final
name|int
name|V_5_0_0_ID
init|=
literal|5000099
decl_stmt|;
DECL|field|V_5_0_0
specifier|public
specifier|static
specifier|final
name|Version
name|V_5_0_0
init|=
operator|new
name|Version
argument_list|(
name|V_5_0_0_ID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LUCENE_6_0_0
argument_list|)
decl_stmt|;
DECL|field|CURRENT
specifier|public
specifier|static
specifier|final
name|Version
name|CURRENT
init|=
name|V_5_0_0
decl_stmt|;
static|static
block|{
assert|assert
name|CURRENT
operator|.
name|luceneVersion
operator|.
name|equals
argument_list|(
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LATEST
argument_list|)
operator|:
literal|"Version must be upgraded to ["
operator|+
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LATEST
operator|+
literal|"] is still set to ["
operator|+
name|CURRENT
operator|.
name|luceneVersion
operator|+
literal|"]"
assert|;
block|}
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
name|V_5_0_0_ID
case|:
return|return
name|V_5_0_0
return|;
case|case
name|V_2_3_0_ID
case|:
return|return
name|V_2_3_0
return|;
case|case
name|V_2_2_1_ID
case|:
return|return
name|V_2_2_1
return|;
case|case
name|V_2_2_0_ID
case|:
return|return
name|V_2_2_0
return|;
case|case
name|V_2_1_2_ID
case|:
return|return
name|V_2_1_2
return|;
case|case
name|V_2_1_1_ID
case|:
return|return
name|V_2_1_1
return|;
case|case
name|V_2_1_0_ID
case|:
return|return
name|V_2_1_0
return|;
case|case
name|V_2_0_2_ID
case|:
return|return
name|V_2_0_2
return|;
case|case
name|V_2_0_1_ID
case|:
return|return
name|V_2_0_1
return|;
case|case
name|V_2_0_0_ID
case|:
return|return
name|V_2_0_0
return|;
case|case
name|V_2_0_0_rc1_ID
case|:
return|return
name|V_2_0_0_rc1
return|;
case|case
name|V_2_0_0_beta2_ID
case|:
return|return
name|V_2_0_0_beta2
return|;
case|case
name|V_2_0_0_beta1_ID
case|:
return|return
name|V_2_0_0_beta1
return|;
default|default:
return|return
operator|new
name|Version
argument_list|(
name|id
argument_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
operator|.
name|LATEST
argument_list|)
return|;
block|}
block|}
comment|/**      * Return the {@link Version} of Elasticsearch that has been used to create an index given its settings.      *      * @throws IllegalStateException if the given index settings doesn't contain a value for the key {@value IndexMetaData#SETTING_VERSION_CREATED}      */
DECL|method|indexCreated
specifier|public
specifier|static
name|Version
name|indexCreated
parameter_list|(
name|Settings
name|indexSettings
parameter_list|)
block|{
specifier|final
name|Version
name|indexVersion
init|=
name|indexSettings
operator|.
name|getAsVersion
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexVersion
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"["
operator|+
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
operator|+
literal|"] is not present in the index settings for index with uuid: ["
operator|+
name|indexSettings
operator|.
name|get
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_INDEX_UUID
argument_list|)
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|indexVersion
return|;
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
comment|/**      * Returns the smallest version between the 2.      */
DECL|method|smallest
specifier|public
specifier|static
name|Version
name|smallest
parameter_list|(
name|Version
name|version1
parameter_list|,
name|Version
name|version2
parameter_list|)
block|{
return|return
name|version1
operator|.
name|id
operator|<
name|version2
operator|.
name|id
condition|?
name|version1
else|:
name|version2
return|;
block|}
comment|/**      * Returns the version given its string representation, current version if the argument is null or empty      */
DECL|method|fromString
specifier|public
specifier|static
name|Version
name|fromString
parameter_list|(
name|String
name|version
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasLength
argument_list|(
name|version
argument_list|)
condition|)
block|{
return|return
name|Version
operator|.
name|CURRENT
return|;
block|}
specifier|final
name|boolean
name|snapshot
decl_stmt|;
comment|// this is some BWC for 2.x and before indices
if|if
condition|(
name|snapshot
operator|=
name|version
operator|.
name|endsWith
argument_list|(
literal|"-SNAPSHOT"
argument_list|)
condition|)
block|{
name|version
operator|=
name|version
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|version
operator|.
name|length
argument_list|()
operator|-
literal|9
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|parts
init|=
name|version
operator|.
name|split
argument_list|(
literal|"\\.|\\-"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
argument_list|<
literal|3
operator|||
name|parts
operator|.
name|length
argument_list|>
literal|4
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"the version needs to contain major, minor, and revision, and optionally the build: "
operator|+
name|version
argument_list|)
throw|;
block|}
try|try
block|{
specifier|final
name|int
name|rawMajor
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|rawMajor
operator|>=
literal|5
operator|&&
name|snapshot
condition|)
block|{
comment|// we don't support snapshot as part of the version here anymore
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal version format - snapshots are only supported until version 2.x"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|betaOffset
init|=
name|rawMajor
operator|<
literal|5
condition|?
literal|0
else|:
literal|25
decl_stmt|;
comment|//we reverse the version id calculation based on some assumption as we can't reliably reverse the modulo
specifier|final
name|int
name|major
init|=
name|rawMajor
operator|*
literal|1000000
decl_stmt|;
specifier|final
name|int
name|minor
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
operator|*
literal|10000
decl_stmt|;
specifier|final
name|int
name|revision
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|2
index|]
argument_list|)
operator|*
literal|100
decl_stmt|;
name|int
name|build
init|=
literal|99
decl_stmt|;
if|if
condition|(
name|parts
operator|.
name|length
operator|==
literal|4
condition|)
block|{
name|String
name|buildStr
init|=
name|parts
index|[
literal|3
index|]
decl_stmt|;
if|if
condition|(
name|buildStr
operator|.
name|startsWith
argument_list|(
literal|"alpha"
argument_list|)
condition|)
block|{
assert|assert
name|rawMajor
operator|>=
literal|5
operator|:
literal|"major must be>= 5 but was "
operator|+
name|major
assert|;
name|build
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|buildStr
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|build
operator|<
literal|25
operator|:
literal|"expected a beta build but "
operator|+
name|build
operator|+
literal|">= 25"
assert|;
block|}
elseif|else
if|if
condition|(
name|buildStr
operator|.
name|startsWith
argument_list|(
literal|"Beta"
argument_list|)
operator|||
name|buildStr
operator|.
name|startsWith
argument_list|(
literal|"beta"
argument_list|)
condition|)
block|{
name|build
operator|=
name|betaOffset
operator|+
name|Integer
operator|.
name|parseInt
argument_list|(
name|buildStr
operator|.
name|substring
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|build
operator|<
literal|50
operator|:
literal|"expected a beta build but "
operator|+
name|build
operator|+
literal|">= 50"
assert|;
block|}
elseif|else
if|if
condition|(
name|buildStr
operator|.
name|startsWith
argument_list|(
literal|"RC"
argument_list|)
operator|||
name|buildStr
operator|.
name|startsWith
argument_list|(
literal|"rc"
argument_list|)
condition|)
block|{
name|build
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|buildStr
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|+
literal|50
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unable to parse version "
operator|+
name|version
argument_list|)
throw|;
block|}
block|}
return|return
name|fromId
argument_list|(
name|major
operator|+
name|minor
operator|+
name|revision
operator|+
name|build
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unable to parse version "
operator|+
name|version
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
DECL|field|luceneVersion
specifier|public
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
name|luceneVersion
decl_stmt|;
DECL|method|Version
name|Version
parameter_list|(
name|int
name|id
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
name|luceneVersion
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
name|luceneVersion
operator|=
name|luceneVersion
expr_stmt|;
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
operator|<
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
operator|<=
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
operator|>
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
operator|>=
name|id
return|;
block|}
comment|/**      * Returns the minimum compatible version based on the current      * version. Ie a node needs to have at least the return version in order      * to communicate with a node running the current version. The returned version      * is in most of the cases the smallest major version release unless the current version      * is a beta or RC release then the version itself is returned.      */
DECL|method|minimumCompatibilityVersion
specifier|public
name|Version
name|minimumCompatibilityVersion
parameter_list|()
block|{
return|return
name|Version
operator|.
name|smallest
argument_list|(
name|this
argument_list|,
name|fromId
argument_list|(
name|major
operator|*
literal|1000000
operator|+
literal|99
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"System.out.*"
argument_list|)
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
literal|"Version: "
operator|+
name|Version
operator|.
name|CURRENT
operator|+
literal|", Build: "
operator|+
name|Build
operator|.
name|CURRENT
operator|.
name|shortHash
argument_list|()
operator|+
literal|"/"
operator|+
name|Build
operator|.
name|CURRENT
operator|.
name|date
argument_list|()
operator|+
literal|", JVM: "
operator|+
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|version
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
name|isAlpha
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"-alpha"
argument_list|)
expr_stmt|;
name|sb
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
name|isBeta
argument_list|()
condition|)
block|{
if|if
condition|(
name|major
operator|>=
literal|2
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"-beta"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|".Beta"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|major
operator|<
literal|5
condition|?
name|build
else|:
name|build
operator|-
literal|25
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
if|if
condition|(
name|major
operator|>=
literal|2
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"-rc"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|".RC"
argument_list|)
expr_stmt|;
block|}
name|sb
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
block|{
return|return
literal|true
return|;
block|}
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
block|{
return|return
literal|false
return|;
block|}
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
block|{
return|return
literal|false
return|;
block|}
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
DECL|method|isBeta
specifier|public
name|boolean
name|isBeta
parameter_list|()
block|{
return|return
name|major
operator|<
literal|5
condition|?
name|build
operator|<
literal|50
else|:
name|build
operator|>=
literal|25
operator|&&
name|build
operator|<
literal|50
return|;
block|}
comment|/**      * Returns true iff this version is an alpha version      * Note: This has been introduced in elasticsearch version 5. Previous versions will never      * have an alpha version.      */
DECL|method|isAlpha
specifier|public
name|boolean
name|isAlpha
parameter_list|()
block|{
return|return
name|major
operator|<
literal|5
condition|?
literal|false
else|:
name|build
operator|<
literal|25
return|;
block|}
DECL|method|isRC
specifier|public
name|boolean
name|isRC
parameter_list|()
block|{
return|return
name|build
operator|>
literal|50
operator|&&
name|build
operator|<
literal|99
return|;
block|}
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|AbstractModule
block|{
DECL|field|version
specifier|private
specifier|final
name|Version
name|version
decl_stmt|;
DECL|method|Module
specifier|public
name|Module
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|Version
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

