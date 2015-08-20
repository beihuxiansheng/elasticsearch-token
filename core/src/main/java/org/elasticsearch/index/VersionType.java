begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|lucene
operator|.
name|uid
operator|.
name|Versions
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_enum
DECL|enum|VersionType
specifier|public
enum|enum
name|VersionType
block|{
DECL|method|INTERNAL
DECL|method|INTERNAL
name|INTERNAL
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isVersionConflictForWrites
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
return|return
name|isVersionConflict
argument_list|(
name|currentVersion
argument_list|,
name|expectedVersion
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isVersionConflictForReads
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
return|return
name|isVersionConflict
argument_list|(
name|currentVersion
argument_list|,
name|expectedVersion
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|isVersionConflict
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_SET
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|expectedVersion
operator|==
name|Versions
operator|.
name|MATCH_ANY
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_FOUND
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|currentVersion
operator|!=
name|expectedVersion
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|updateVersion
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
return|return
operator|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_SET
operator|||
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_FOUND
operator|)
condition|?
literal|1
else|:
name|currentVersion
operator|+
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validateVersionForWrites
parameter_list|(
name|long
name|version
parameter_list|)
block|{
comment|// not allowing Versions.NOT_FOUND as it is not a valid input value.
return|return
name|version
operator|>
literal|0L
operator|||
name|version
operator|==
name|Versions
operator|.
name|MATCH_ANY
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validateVersionForReads
parameter_list|(
name|long
name|version
parameter_list|)
block|{
comment|// not allowing Versions.NOT_FOUND as it is not a valid input value.
return|return
name|version
operator|>
literal|0L
operator|||
name|version
operator|==
name|Versions
operator|.
name|MATCH_ANY
return|;
block|}
annotation|@
name|Override
specifier|public
name|VersionType
name|versionTypeForReplicationAndRecovery
parameter_list|()
block|{
comment|// replicas get the version from the primary after increment. The same version is stored in
comment|// the transaction log. -> the should use the external semantics.
return|return
name|EXTERNAL
return|;
block|}
block|}
block|,
DECL|method|EXTERNAL
DECL|method|EXTERNAL
name|EXTERNAL
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isVersionConflictForWrites
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_SET
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_FOUND
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|expectedVersion
operator|==
name|Versions
operator|.
name|MATCH_ANY
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|currentVersion
operator|>=
name|expectedVersion
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isVersionConflictForReads
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_SET
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|expectedVersion
operator|==
name|Versions
operator|.
name|MATCH_ANY
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_FOUND
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|currentVersion
operator|!=
name|expectedVersion
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|updateVersion
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
return|return
name|expectedVersion
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validateVersionForWrites
parameter_list|(
name|long
name|version
parameter_list|)
block|{
return|return
name|version
operator|>=
literal|0L
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validateVersionForReads
parameter_list|(
name|long
name|version
parameter_list|)
block|{
return|return
name|version
operator|>=
literal|0L
operator|||
name|version
operator|==
name|Versions
operator|.
name|MATCH_ANY
return|;
block|}
block|}
block|,
DECL|method|EXTERNAL_GTE
DECL|method|EXTERNAL_GTE
name|EXTERNAL_GTE
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isVersionConflictForWrites
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_SET
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_FOUND
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|expectedVersion
operator|==
name|Versions
operator|.
name|MATCH_ANY
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|currentVersion
operator|>
name|expectedVersion
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isVersionConflictForReads
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_SET
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|expectedVersion
operator|==
name|Versions
operator|.
name|MATCH_ANY
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_FOUND
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|currentVersion
operator|!=
name|expectedVersion
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|updateVersion
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
return|return
name|expectedVersion
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validateVersionForWrites
parameter_list|(
name|long
name|version
parameter_list|)
block|{
return|return
name|version
operator|>=
literal|0L
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validateVersionForReads
parameter_list|(
name|long
name|version
parameter_list|)
block|{
return|return
name|version
operator|>=
literal|0L
operator|||
name|version
operator|==
name|Versions
operator|.
name|MATCH_ANY
return|;
block|}
block|}
block|,
comment|/**      * Warning: this version type should be used with care. Concurrent indexing may result in loss of data on replicas      */
DECL|method|FORCE
DECL|method|FORCE
name|FORCE
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isVersionConflictForWrites
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_SET
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|currentVersion
operator|==
name|Versions
operator|.
name|NOT_FOUND
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|expectedVersion
operator|==
name|Versions
operator|.
name|MATCH_ANY
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isVersionConflictForReads
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|updateVersion
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
block|{
return|return
name|expectedVersion
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validateVersionForWrites
parameter_list|(
name|long
name|version
parameter_list|)
block|{
return|return
name|version
operator|>=
literal|0L
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validateVersionForReads
parameter_list|(
name|long
name|version
parameter_list|)
block|{
return|return
name|version
operator|>=
literal|0L
operator|||
name|version
operator|==
name|Versions
operator|.
name|MATCH_ANY
return|;
block|}
block|}
block|;
DECL|field|value
specifier|private
specifier|final
name|byte
name|value
decl_stmt|;
DECL|method|VersionType
name|VersionType
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|getValue
specifier|public
name|byte
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**      * Checks whether the current version conflicts with the expected version, based on the current version type.      *      * @return true if versions conflict false o.w.      */
DECL|method|isVersionConflictForWrites
specifier|public
specifier|abstract
name|boolean
name|isVersionConflictForWrites
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
function_decl|;
comment|/**      * Checks whether the current version conflicts with the expected version, based on the current version type.      *      * @return true if versions conflict false o.w.      */
DECL|method|isVersionConflictForReads
specifier|public
specifier|abstract
name|boolean
name|isVersionConflictForReads
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
function_decl|;
comment|/**      * Returns the new version for a document, based on its current one and the specified in the request      *      * @return new version      */
DECL|method|updateVersion
specifier|public
specifier|abstract
name|long
name|updateVersion
parameter_list|(
name|long
name|currentVersion
parameter_list|,
name|long
name|expectedVersion
parameter_list|)
function_decl|;
comment|/**      * validate the version is a valid value for this type when writing.      *      * @return true if valid, false o.w      */
DECL|method|validateVersionForWrites
specifier|public
specifier|abstract
name|boolean
name|validateVersionForWrites
parameter_list|(
name|long
name|version
parameter_list|)
function_decl|;
comment|/**      * validate the version is a valid value for this type when reading.      *      * @return true if valid, false o.w      */
DECL|method|validateVersionForReads
specifier|public
specifier|abstract
name|boolean
name|validateVersionForReads
parameter_list|(
name|long
name|version
parameter_list|)
function_decl|;
comment|/**      * Some version types require different semantics for primary and replicas. This version allows      * the type to override the default behavior.      */
DECL|method|versionTypeForReplicationAndRecovery
specifier|public
name|VersionType
name|versionTypeForReplicationAndRecovery
parameter_list|()
block|{
return|return
name|this
return|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|VersionType
name|fromString
parameter_list|(
name|String
name|versionType
parameter_list|)
block|{
if|if
condition|(
literal|"internal"
operator|.
name|equals
argument_list|(
name|versionType
argument_list|)
condition|)
block|{
return|return
name|INTERNAL
return|;
block|}
elseif|else
if|if
condition|(
literal|"external"
operator|.
name|equals
argument_list|(
name|versionType
argument_list|)
condition|)
block|{
return|return
name|EXTERNAL
return|;
block|}
elseif|else
if|if
condition|(
literal|"external_gt"
operator|.
name|equals
argument_list|(
name|versionType
argument_list|)
condition|)
block|{
return|return
name|EXTERNAL
return|;
block|}
elseif|else
if|if
condition|(
literal|"external_gte"
operator|.
name|equals
argument_list|(
name|versionType
argument_list|)
condition|)
block|{
return|return
name|EXTERNAL_GTE
return|;
block|}
elseif|else
if|if
condition|(
literal|"force"
operator|.
name|equals
argument_list|(
name|versionType
argument_list|)
condition|)
block|{
return|return
name|FORCE
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No version type match ["
operator|+
name|versionType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|VersionType
name|fromString
parameter_list|(
name|String
name|versionType
parameter_list|,
name|VersionType
name|defaultVersionType
parameter_list|)
block|{
if|if
condition|(
name|versionType
operator|==
literal|null
condition|)
block|{
return|return
name|defaultVersionType
return|;
block|}
return|return
name|fromString
argument_list|(
name|versionType
argument_list|)
return|;
block|}
DECL|method|fromValue
specifier|public
specifier|static
name|VersionType
name|fromValue
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|0
condition|)
block|{
return|return
name|INTERNAL
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|1
condition|)
block|{
return|return
name|EXTERNAL
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|2
condition|)
block|{
return|return
name|EXTERNAL_GTE
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|==
literal|3
condition|)
block|{
return|return
name|FORCE
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No version type match ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
end_enum

end_unit
