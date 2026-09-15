const technicalDetails = /ConstraintViolationException|DataIntegrityViolationException|PSQLException|foreign key constraint/i;

export function maintenanceError(error: unknown, fallback: string, unavailable: string): string {
  const response = error as { status?: number; message?: string } | null;
  if (response?.status === 404) return unavailable;
  const message = response?.message?.trim();
  return message && !technicalDetails.test(message) ? message : fallback;
}
