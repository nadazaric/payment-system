import PaymentPage from "@/components/payment/PaymentPage";

type PaymentRoutePageProps = {
    params: Promise<{
        paymentId: string;
    }>;
};

export default async function PaymentRoutePage({
    params
}: PaymentRoutePageProps) {
    const { paymentId } = await params;

    return <PaymentPage paymentId={paymentId} />;
}