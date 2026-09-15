export type Mercado = 'BRASIL' | 'EUA';
export type Moeda = 'BRL' | 'USD';
export type TipoOperacao = 'COMPRA' | 'VENDA';

export interface Usuario { id: number; nome: string; email: string; createdAt: string; updatedAt: string; }
export interface LoginRequest { email: string; senha: string; }
export interface AuthenticatedUser { id: number; nome: string; email: string; }
export type LoginResponse = AuthenticatedUser;
export interface UsuarioRequest { nome: string; email: string; senha: string; }
export interface UsuarioUpdateRequest { nome: string; email: string; senha?: string; }
export interface Acao { id: number; ticker: string; nomeEmpresa: string; mercado: Mercado; moeda: Moeda; cotacaoAtual: number; dataHoraCotacao: string; createdAt: string; updatedAt: string; }
export interface Corretora { id: number; cnpj: string; razaoSocial: string; nomeFantasia: string; email: string; telefone: string; cep: string; logradouro: string; numero: string; complemento: string; bairro: string; cidade: string; uf: string; situacaoCadastral: string; registroCvm: string; dataValidacaoCvm: string; createdAt: string; updatedAt: string; }
export interface CorretoraRequest { cnpj: string; }
export interface Carteira { id: number; nome: string; usuarioId: number; corretoraId: number; createdAt: string; updatedAt: string; }
export interface CarteiraRequest { nome: string; usuarioId: number; corretoraId: number; }
export interface CarteiraUpdateRequest { nome: string; }
export interface CarteiraAcao { id: number; carteiraId: number; acaoId: number; ticker: string; quantidade: number; version: number; }
export interface PosicaoAcao { carteiraId: number; acaoId: number; ticker: string; nomeEmpresa: string; mercado: Mercado; moeda: Moeda; quantidade: number; cotacaoAtual: number | null; dataHoraCotacao: string | null; valorAtual: number | null; precoMedio: number | null; valorInvestidoAtual?: number | null; valorTotalComprado?: number | null; valorTotalVendido?: number | null; cotacaoEmBrl?: number | null; valorAtualEmBrl?: number | null; taxaUsdBrl?: number | null; dataHoraTaxaUsdBrl?: string | null; precoMedioVenda: number | null; lucroPrejuizoRealizado: number | null; lucroPrejuizoNaoRealizado: number | null; lucroPrejuizo: number | null; }
export interface ResumoPorMoeda { moeda: Moeda; valorAtual: number | null; lucroPrejuizo: number | null; }
export interface ResumoCarteira { carteiraId: number; nome: string; quantidadeAtivos: number; resumosPorMoeda: ResumoPorMoeda[]; }
export interface HistoricoCotacao { id: number; acaoId: number; cotacao: number; dataHora: string; }
export interface HistoricoCotacaoParams extends PageParams { inicio?: string; fim?: string; }
export interface AtualizacaoCotacoes { total: number; atualizadas: number; falhas: number; dataHora: string; }
/**
 * Valores e identificação retornados pelo backend para uma operação já
 * concluída. O frontend apenas apresenta esses valores; não os reconstrói a
 * partir das posições ou de outras operações.
 */
export interface Operacao {
  id: number;
  carteiraId: number;
  acaoId: number;
  ticker: string;
  nomeEmpresa: string;
  mercado: Mercado;
  moeda: Moeda;
  tipo: TipoOperacao;
  quantidade: number;
  precoUnitario: number;
  valorBruto: number;
  corretagem: number;
  impostos: number;
  valorAdicional: number;
  valorLiquido: number;
  dataHora: string;
  createdAt: string;
}
export interface CompraRequest { carteiraId: number; acaoId?: number; ticker?: string; mercado?: Mercado; quantidade: number; precoUnitario?: number; corretagem?: number; impostos?: number; valorAdicional?: number; }
export interface VendaRequest { carteiraId: number; acaoId: number; quantidade: number; corretagem?: number; impostos?: number; valorAdicional?: number; }
export interface PageParams { page?: number; size?: number; sort?: string | readonly string[]; }
export const DEFAULT_PAGE_PARAMS: Required<PageParams> = { page: 0, size: 20, sort: 'id,desc' };
export type Pageable = PageParams;
export interface Page<T> { content: T[]; totalElements: number; totalPages: number; size: number; number: number; first: boolean; last: boolean; numberOfElements: number; empty: boolean; }
export interface ApiError { timestamp?: string; status: number; error?: string; message: string; path?: string; }
