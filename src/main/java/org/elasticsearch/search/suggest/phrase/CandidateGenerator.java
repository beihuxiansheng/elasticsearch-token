begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest.phrase
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|phrase
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|phrase
operator|.
name|DirectCandidateGenerator
operator|.
name|Candidate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|phrase
operator|.
name|DirectCandidateGenerator
operator|.
name|CandidateSet
import|;
end_import

begin_comment
comment|//TODO public for tests
end_comment

begin_class
DECL|class|CandidateGenerator
specifier|public
specifier|abstract
class|class
name|CandidateGenerator
block|{
DECL|method|isKnownWord
specifier|public
specifier|abstract
name|boolean
name|isKnownWord
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|frequency
specifier|public
specifier|abstract
name|long
name|frequency
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|drawCandidates
specifier|public
name|CandidateSet
name|drawCandidates
parameter_list|(
name|BytesRef
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|CandidateSet
name|set
init|=
operator|new
name|CandidateSet
argument_list|(
name|Candidate
operator|.
name|EMPTY
argument_list|,
name|createCandidate
argument_list|(
name|term
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|drawCandidates
argument_list|(
name|set
argument_list|)
return|;
block|}
DECL|method|createCandidate
specifier|public
name|Candidate
name|createCandidate
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|boolean
name|userInput
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createCandidate
argument_list|(
name|term
argument_list|,
name|frequency
argument_list|(
name|term
argument_list|)
argument_list|,
literal|1.0
argument_list|,
name|userInput
argument_list|)
return|;
block|}
DECL|method|createCandidate
specifier|public
name|Candidate
name|createCandidate
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|long
name|frequency
parameter_list|,
name|double
name|channelScore
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createCandidate
argument_list|(
name|term
argument_list|,
name|frequency
argument_list|,
name|channelScore
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|createCandidate
specifier|public
specifier|abstract
name|Candidate
name|createCandidate
parameter_list|(
name|BytesRef
name|term
parameter_list|,
name|long
name|frequency
parameter_list|,
name|double
name|channelScore
parameter_list|,
name|boolean
name|userInput
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|drawCandidates
specifier|public
specifier|abstract
name|CandidateSet
name|drawCandidates
parameter_list|(
name|CandidateSet
name|set
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

